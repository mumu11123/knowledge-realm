package com.knowledge.realm.ai.learning;

import com.knowledge.realm.ai.*;
import com.knowledge.realm.vo.Views;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AuthorizedLearningService {
    private final AiMapper mapper;
    public AuthorizedLearningService(AiMapper mapper) { this.mapper=mapper; }
    @Transactional
    public void record(long learner, Views.Answer answer) {
        mapper.attempt(learner,answer.storyId(),answer.questionId(),answer.selectedOptionId(),answer.correct());
    }
    public AiContracts.Progress progress(long learner) { return mapper.progress(learner); }
    public List<AiContracts.WrongPoint> wrong(long learner) { return mapper.wrong(learner); }
    public List<AiContracts.ReviewItem> reviewPlan(long learner) {
        return wrong(learner).stream().limit(5).map(k -> new AiContracts.ReviewItem(k.knowledgeId(),k.name(),
            "先复述定义与适用条件，再重做关联故事并比较每个选项；这是复习建议，不会修改学习记录。")).toList();
    }
}
