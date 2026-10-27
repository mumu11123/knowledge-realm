package com.knowledge.realm.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.realm.ai.knowledge.KnowledgeRetrievalService;
import com.knowledge.realm.ai.model.*;
import com.knowledge.realm.ai.tutor.TutorService;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TutorServiceTest {
    @Test void noReliableKnowledgeNeverCallsModel() {
        AiMapper mapper=mock(AiMapper.class);var retrieval=mock(KnowledgeRetrievalService.class);var gateway=mock(ModelGateway.class);
        var properties=new AiProperties();properties.setEnabled(true);properties.setApiKey("test-only");
        when(retrieval.scope(201)).thenReturn(Set.of(201L));
        var json=new ObjectMapper();var executor=new CallExecutor(properties);
        try {
            var reply=new TutorService(mapper,properties,retrieval,gateway,executor,new GroundedAnswerValidator(json),json,new SimpleMeterRegistry()).ask(1,new AiContracts.Ask(201,"离题问题"));
            assertThat(reply.status()).isEqualTo("NO_RELIABLE_KNOWLEDGE");verifyNoInteractions(gateway);
        } finally {executor.close();}
    }
    @Test void providerFailureIsExplicitAndDoesNotInventAnswer() throws Exception {
        AiMapper mapper=mock(AiMapper.class);var retrieval=mock(KnowledgeRetrievalService.class);var gateway=mock(ModelGateway.class);
        var properties=new AiProperties();properties.setEnabled(true);properties.setApiKey("test-only");
        when(retrieval.scope(201)).thenReturn(Set.of(201L));when(retrieval.retrieve(201,"链表")).thenReturn(List.of(GroundedAnswerValidatorTest.SOURCE));
        when(gateway.call(anyString(),anyString(),any())).thenThrow(new RuntimeException("test provider unavailable"));
        var json=new ObjectMapper();var executor=new CallExecutor(properties);
        try {
            var reply=new TutorService(mapper,properties,retrieval,gateway,executor,new GroundedAnswerValidator(json),json,new SimpleMeterRegistry()).ask(1,new AiContracts.Ask(201,"链表"));
            assertThat(reply.status()).isEqualTo("MODEL_UNAVAILABLE");assertThat(reply.claims()).isEmpty();assertThat(reply.sources()).hasSize(1);
        } finally {executor.close();}
    }
    @Test void timeoutCancelsWorkAndReturnsAnExplicitFallback() throws Exception {
        AiMapper mapper=mock(AiMapper.class);var retrieval=mock(KnowledgeRetrievalService.class);var gateway=mock(ModelGateway.class);
        var properties=new AiProperties();properties.setEnabled(true);properties.setApiKey("test-only");properties.setTimeoutSeconds(2);
        when(retrieval.scope(201)).thenReturn(Set.of(201L));when(retrieval.retrieve(201,"链表")).thenReturn(List.of(GroundedAnswerValidatorTest.SOURCE));
        when(gateway.call(anyString(),anyString(),any())).thenAnswer(call -> {Thread.sleep(5000);return null;});
        var json=new ObjectMapper();var executor=new CallExecutor(properties);
        try {
            var reply=new TutorService(mapper,properties,retrieval,gateway,executor,new GroundedAnswerValidator(json),json,new SimpleMeterRegistry()).ask(1,new AiContracts.Ask(201,"链表"));
            assertThat(reply.status()).isEqualTo("MODEL_TIMEOUT");assertThat(reply.claims()).isEmpty();assertThat(reply.elapsedMs()).isLessThan(4000);
        } finally {executor.close();}
    }
    @Test void semanticEvidenceRejectionIsNotPresentedAsAnAnswer() {
        AiMapper mapper=mock(AiMapper.class);var retrieval=mock(KnowledgeRetrievalService.class);var gateway=mock(ModelGateway.class);
        var properties=new AiProperties();properties.setEnabled(true);properties.setApiKey("test-only");
        when(retrieval.scope(201)).thenReturn(Set.of(201L));when(retrieval.retrieve(201,"链表")).thenReturn(List.of(GroundedAnswerValidatorTest.SOURCE));
        when(gateway.call(anyString(),anyString(),any())).thenReturn(
            new ModelGateway.Result("{\"insufficient\":false,\"claims\":[{\"text\":\"错误结论\",\"sourceId\":\"K1\",\"quote\":\"单链表的结点由指针链接\"}]}",1,1),
            new ModelGateway.Result("{\"supported\":false,\"unsupportedClaims\":[0]}",1,1));
        var json=new ObjectMapper();var executor=new CallExecutor(properties);
        try {
            var reply=new TutorService(mapper,properties,retrieval,gateway,executor,new GroundedAnswerValidator(json),json,new SimpleMeterRegistry()).ask(1,new AiContracts.Ask(201,"链表"));
            assertThat(reply.status()).isEqualTo("UNVERIFIED_ANSWER");assertThat(reply.claims()).isEmpty();
        } finally {executor.close();}
    }
}
