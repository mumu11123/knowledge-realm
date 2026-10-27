package com.knowledge.realm.ai.model;

import com.knowledge.realm.ai.AiProperties;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import java.util.concurrent.*;

@Component
public class CallExecutor {
    private final ThreadPoolExecutor executor=new ThreadPoolExecutor(0,4,30,TimeUnit.SECONDS,new SynchronousQueue<>(),
        r -> { Thread t=new Thread(r,"realm-ai-call"); t.setDaemon(true); return t; },new ThreadPoolExecutor.AbortPolicy());
    private final AiProperties properties;
    public CallExecutor(AiProperties properties) { this.properties=properties; }
    public <T> T run(Callable<T> action,AgentContext context) throws Exception {
        Future<T> future=executor.submit(action);
        try { return future.get(properties.getTimeoutSeconds(),TimeUnit.SECONDS); }
        catch(TimeoutException e) { if(context!=null) context.cancel(); future.cancel(true); throw e; }
        catch(InterruptedException e) { if(context!=null) context.cancel(); future.cancel(true); Thread.currentThread().interrupt(); throw e; }
    }
    @PreDestroy public void close() { executor.shutdownNow(); }
}
