package com.knowledge.realm.ai.model;

import com.knowledge.realm.ai.AiProperties;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.*;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.*;

@Component
public class ModelGateway {
    public record Result(String content,int promptTokens,int completionTokens) {}
    private final AiProperties properties;
    private final LearningTools tools;
    public ModelGateway(AiProperties properties, LearningTools tools) { this.properties=properties; this.tools=tools; }
    public Result call(String system,String user,AgentContext context) {
        if(!properties.configured()) throw new IllegalStateException("MODEL_NOT_CONFIGURED");
        var factory=new JdkClientHttpRequestFactory(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        factory.setReadTimeout(Duration.ofSeconds(Math.min(30,properties.getTimeoutSeconds())));
        OpenAiApi api=OpenAiApi.builder().baseUrl(properties.getBaseUrl()).completionsPath("/chat/completions")
            .apiKey(properties.getApiKey()).restClientBuilder(RestClient.builder().requestFactory(factory)).build();
        var model=OpenAiChatModel.builder().openAiApi(api)
            .retryTemplate(RetryTemplate.builder().maxAttempts(1).build()).build();
        var options=OpenAiChatOptions.builder().model(properties.getModel()).temperature(0.2).maxTokens(2000)
            .extraBody(Map.of("thinking",Map.of("type","disabled")))
            .internalToolExecutionEnabled(false);
        if(context!=null) options.toolCallbacks(ToolCallbacks.from(tools)).toolContext(Map.of("request",context));
        var chatOptions=options.build();
        Prompt prompt=new Prompt(List.of(new SystemMessage(system),new UserMessage(user)),chatOptions);
        ToolCallingManager manager=ToolCallingManager.builder().build();
        int input=0,output=0;
        for(int turn=0;turn<4;turn++) {
            if(Thread.currentThread().isInterrupted()) throw new IllegalStateException("CANCELLED");
            ChatResponse response=model.call(prompt);
            var usage=response.getMetadata().getUsage();
            if(usage!=null) {
                int in=Objects.requireNonNullElse(usage.getPromptTokens(),0),out=Objects.requireNonNullElse(usage.getCompletionTokens(),0);
                input+=in; output+=out; if(context!=null) context.usage(in,out);
            }
            if(!response.hasToolCalls()) return new Result(response.getResult().getOutput().getText(),input,output);
            if(context==null || turn==3) throw new IllegalStateException("TOOL_BUDGET_EXCEEDED");
            var execution=manager.executeToolCalls(prompt,response);
            prompt=new Prompt(execution.conversationHistory(),chatOptions);
        }
        throw new IllegalStateException("TOOL_BUDGET_EXCEEDED");
    }
}
