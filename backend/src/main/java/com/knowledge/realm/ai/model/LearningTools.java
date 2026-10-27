package com.knowledge.realm.ai.model;

import com.knowledge.realm.ai.*;
import com.knowledge.realm.ai.learning.AuthorizedLearningService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Function;

@Component
public class LearningTools {
    private final AuthorizedLearningService learning;
    public LearningTools(AuthorizedLearningService learning) { this.learning=learning; }
    private <T> T invoke(ToolContext toolContext,String name,Function<AgentContext,T> action) {
        AgentContext context=(AgentContext)toolContext.getContext().get("request");
        if(context==null) throw new IllegalStateException("MISSING_AUTHORIZED_CONTEXT");
        context.tool(name);
        try { T result=action.apply(context); context.completed(name,result); return result; }
        catch(RuntimeException e) { context.failed(name); throw e; }
    }
    @Tool(description="读取当前学习知识点的已审核专业片段及来源；不读取演示故事。")
    public List<AiContracts.Source> getKnowledgePoint(ToolContext toolContext) {
        return invoke(toolContext,"getKnowledgePoint",c -> c.sources.stream().filter(s -> s.knowledgeId()==c.knowledgeId).toList());
    }
    @Tool(description="读取当前服务端授权学习者的真实答题统计；没有记录时返回零，不接收用户 ID。")
    public AiContracts.Progress getLearningProgress(ToolContext toolContext) {
        return invoke(toolContext,"getLearningProgress",c -> learning.progress(c.learnerId));
    }
    @Tool(description="读取当前授权学习者答错的知识点；只有本次浏览器会话建立后的服务器记录，不接收用户 ID。")
    public List<AiContracts.WrongPoint> getWrongKnowledgePoints(ToolContext toolContext) {
        return invoke(toolContext,"getWrongKnowledgePoints",c -> learning.wrong(c.learnerId));
    }
    @Tool(description="读取与当前知识点相关且本次检索命中的已审核片段；没有时明确返回空集合。")
    public List<AiContracts.Source> getRelatedKnowledgePoints(ToolContext toolContext) {
        return invoke(toolContext,"getRelatedKnowledgePoints",c -> c.sources.stream().filter(s -> s.knowledgeId()!=c.knowledgeId).toList());
    }
    @Tool(description="根据当前授权学习者的真实错题知识点生成只读复习建议；没有错题时返回空集合，不修改记录。")
    public List<AiContracts.ReviewItem> generateReviewPlan(ToolContext toolContext) {
        return invoke(toolContext,"generateReviewPlan",c -> learning.reviewPlan(c.learnerId));
    }
}
