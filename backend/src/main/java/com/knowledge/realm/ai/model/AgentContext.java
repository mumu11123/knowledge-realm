package com.knowledge.realm.ai.model;

import com.knowledge.realm.ai.AiContracts;
import java.util.*;

/** Per request, supplied by server only. Never deserialized from model arguments. */
public final class AgentContext {
    public final long learnerId;
    public final long knowledgeId;
    public final Set<Long> allowedKnowledge;
    public final List<AiContracts.Source> sources;
    private final List<AiContracts.ToolEvent> events=new ArrayList<>();
    private int toolCount;
    private int inputTokens;
    private int outputTokens;
    private final Map<String,Object> results=new LinkedHashMap<>();
    private volatile boolean cancelled;
    public AgentContext(long learnerId,long knowledgeId,Set<Long> allowed,List<AiContracts.Source> sources) {
        this.learnerId=learnerId; this.knowledgeId=knowledgeId; this.allowedKnowledge=Set.copyOf(allowed); this.sources=List.copyOf(sources);
    }
    public synchronized void tool(String name) {
        if (cancelled || Thread.currentThread().isInterrupted() || ++toolCount>8) throw new IllegalStateException("TOOL_BUDGET_EXCEEDED");
    }
    public synchronized void completed(String name,Object result) { events.add(new AiContracts.ToolEvent(name,"EXECUTED")); results.put(name,result); }
    public synchronized void failed(String name) { events.add(new AiContracts.ToolEvent(name,"FAILED")); }
    public synchronized Map<String,Object> results() { return Map.copyOf(results); }
    public synchronized void usage(int input,int output) { inputTokens+=input; outputTokens+=output; }
    public synchronized int inputTokens() { return inputTokens; }
    public synchronized int outputTokens() { return outputTokens; }
    public void cancel() { cancelled=true; }
    public synchronized List<AiContracts.ToolEvent> events() { return List.copyOf(events); }
}
