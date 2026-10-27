package com.knowledge.realm.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.realm.ai.model.*;
import com.knowledge.realm.ai.learning.AuthorizedLearningService;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Real Spring AI + HTTP + ToolCallingManager, with a test-only protocol server, never a production fallback. */
class ModelToolProtocolTest {
    @Test void actualProviderHttpFailureReturnsGroundedDegradation() throws Exception {
        HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/chat/completions",exchange -> {
            byte[] error="{\"error\":{\"message\":\"test provider unavailable\",\"type\":\"server_error\"}}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type","application/json");exchange.sendResponseHeaders(503,error.length);exchange.getResponseBody().write(error);exchange.close();
        });server.start();
        var properties=new AiProperties();properties.setEnabled(true);properties.setApiKey("test-only");properties.setBaseUrl("http://127.0.0.1:"+server.getAddress().getPort());
        var mapper=mock(AiMapper.class);var retrieval=mock(com.knowledge.realm.ai.knowledge.KnowledgeRetrievalService.class);
        when(retrieval.scope(201)).thenReturn(Set.of(201L));when(retrieval.retrieve(201,"单链表")).thenReturn(List.of(GroundedAnswerValidatorTest.SOURCE));
        var gateway=new ModelGateway(properties,new LearningTools(new AuthorizedLearningService(mapper)));
        var executor=new CallExecutor(properties);var json=new ObjectMapper();
        try {
            var service=new com.knowledge.realm.ai.tutor.TutorService(mapper,properties,retrieval,gateway,executor,new GroundedAnswerValidator(json),json,new io.micrometer.core.instrument.simple.SimpleMeterRegistry());
            var reply=service.ask(17,new AiContracts.Ask(201,"单链表"));
            assertThat(reply.status()).isEqualTo("MODEL_UNAVAILABLE");assertThat(reply.claims()).isEmpty();assertThat(reply.sources()).hasSize(1);
        } finally {executor.close();server.stop(0);}
    }
    @Test void toolSchemasNeverExposeIdentityOrMutationOperations() {
        var tools=new LearningTools(new AuthorizedLearningService(mock(AiMapper.class)));
        var callbacks=org.springframework.ai.support.ToolCallbacks.from(tools);
        assertThat(Arrays.stream(callbacks).map(c -> c.getToolDefinition().name()).toList())
            .containsExactlyInAnyOrder("getKnowledgePoint","getWrongKnowledgePoints","getLearningProgress","getRelatedKnowledgePoints","generateReviewPlan");
        for(var callback:callbacks) assertThat(callback.getToolDefinition().inputSchema()).doesNotContain("learnerId","userId","ToolContext");
    }
    @Test void springAiExecutesRealBusinessToolUsingServerIdentityDespiteForgedArguments() throws Exception {
        AiMapper mapper=mock(AiMapper.class);
        when(mapper.progress(17)).thenReturn(new AiContracts.Progress(7,3,2));
        var learning=new AuthorizedLearningService(mapper);
        var tools=new LearningTools(learning);
        List<String> requests=new CopyOnWriteArrayList<>();
        HttpServer server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        server.createContext("/chat/completions",exchange -> {
            String body=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8); requests.add(body);
            String message=requests.size()==1?
                "{\"role\":\"assistant\",\"content\":null,\"tool_calls\":[{\"id\":\"call_1\",\"type\":\"function\",\"function\":{\"name\":\"getLearningProgress\",\"arguments\":\"{\\\"learnerId\\\":999}\"}}]}":
                "{\"role\":\"assistant\",\"content\":\"{\\\"insufficient\\\":false,\\\"claims\\\":[{\\\"text\\\":\\\"沿链接访问\\\",\\\"sourceId\\\":\\\"K1\\\",\\\"quote\\\":\\\"单链表的结点由指针链接\\\"}]}\"}";
            String response="{\"id\":\"test\",\"object\":\"chat.completion\",\"created\":1,\"model\":\"test-model\",\"choices\":[{\"index\":0,\"message\":"+message+",\"finish_reason\":\""+(requests.size()==1?"tool_calls":"stop")+"\"}],\"usage\":{\"prompt_tokens\":11,\"completion_tokens\":3,\"total_tokens\":14}}";
            byte[] bytes=response.getBytes(StandardCharsets.UTF_8); exchange.getResponseHeaders().set("Content-Type","application/json"); exchange.sendResponseHeaders(200,bytes.length);exchange.getResponseBody().write(bytes);exchange.close();
        });
        server.start();
        try {
            var properties=new AiProperties();properties.setEnabled(true);properties.setApiKey("test-only-not-real");properties.setBaseUrl("http://127.0.0.1:"+server.getAddress().getPort());
            var context=new AgentContext(17,201,Set.of(201L),List.of(GroundedAnswerValidatorTest.SOURCE));
            var result=new ModelGateway(properties,tools).call("test","问进度",context);
            assertThat(requests).hasSize(2);
            assertThat(requests.get(1)).contains("\\\"attempts\\\":7");
            assertThat(context.events()).containsExactly(new AiContracts.ToolEvent("getLearningProgress","EXECUTED"));
            verify(mapper).progress(17);verify(mapper,never()).progress(999);
            assertThat(result.promptTokens()).isEqualTo(22);assertThat(result.completionTokens()).isEqualTo(6);
            assertThat(new GroundedAnswerValidator(new ObjectMapper()).validate(result.content(),context.sources).claims()).hasSize(1);
        } finally {server.stop(0);}
    }
    @Test void toolsHaveBudgetAndCancellation() {
        var context=new AgentContext(17,201,Set.of(201L),List.of());
        for(int i=0;i<8;i++) context.tool("tool");
        assertThatThrownBy(() -> context.tool("tool")).isInstanceOf(IllegalStateException.class);
        var cancelled=new AgentContext(17,201,Set.of(201L),List.of());cancelled.cancel();
        assertThatThrownBy(() -> cancelled.tool("tool")).isInstanceOf(IllegalStateException.class);
    }
}
