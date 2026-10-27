package com.knowledge.realm.service;

import com.knowledge.realm.dto.AnswerRequest;
import com.knowledge.realm.entity.Rows;
import com.knowledge.realm.exception.BusinessException;
import com.knowledge.realm.mapper.LearningMapper;
import com.knowledge.realm.vo.Views;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LearningService {
    private final LearningMapper mapper;
    public LearningService(LearningMapper mapper) { this.mapper = mapper; }

    public List<Views.Subject> subjects() {
        return mapper.subjects().stream().map(s -> new Views.Subject(s.id(), s.code(), s.name(), s.description(), s.availability())).toList();
    }
    public List<Views.Chapter> chapters(long subjectId) {
        if (mapper.subject(subjectId) == null) throw BusinessException.notFound();
        return mapper.chapters(subjectId).stream().map(this::chapterView).toList();
    }
    private Views.Chapter chapterView(Rows.Chapter c) { return new Views.Chapter(c.id(), c.subjectId(), c.parentId(), c.name(), c.sortOrder()); }
    public List<Views.Knowledge> knowledgeInChapter(long chapterId) {
        if (mapper.chapter(chapterId) == null) throw BusinessException.notFound();
        return mapper.knowledgeInChapter(chapterId).stream().map(this::knowledgeView).toList();
    }
    public Views.Knowledge knowledge(long id) { return knowledgeView(requireKnowledge(id)); }
    private Rows.Knowledge requireKnowledge(long id) {
        Rows.Knowledge k = mapper.knowledge(id);
        if (k == null) throw BusinessException.notFound();
        return k;
    }
    private Views.Knowledge knowledgeView(Rows.Knowledge k) {
        return new Views.Knowledge(k.id(), k.chapterId(), k.name(), k.introduction(), k.resourceStatus(), k.reviewStatus(),
            k.contentSource(), k.syllabusVersion(), mapper.prerequisites(k.id()), mapper.related(k.id()), links(k.id()));
    }
    private boolean accessible(String resourceStatus, String reviewStatus) {
        return "DEMO".equals(resourceStatus) || ("PUBLISHED".equals(resourceStatus) && "REVIEWED".equals(reviewStatus));
    }
    private Rows.Story requireStory(long id) {
        Rows.Story s = mapper.story(id);
        if (s == null || !accessible(s.resourceStatus(), s.reviewStatus())) throw BusinessException.notFound();
        return s;
    }
    private List<Views.StoryLink> links(long knowledgeId) {
        return mapper.storiesForKnowledge(knowledgeId).stream().map(s -> new Views.StoryLink(s.id(), s.title())).toList();
    }
    private Rows.Question singleQuestion(long storyId) {
        List<Rows.Question> questions = mapper.questions(storyId);
        // V0.1 deliberately supports one decision per story. Reject invalid content rather than leaking later scenes.
        if (questions.size() != 1) throw BusinessException.notFound();
        return questions.getFirst();
    }
    public Views.Story story(long id) {
        Rows.Story s = requireStory(id);
        Rows.Question q = singleQuestion(id);
        List<Views.Option> options = mapper.options(q.id()).stream().map(o -> new Views.Option(o.id(), o.label(), o.name())).toList();
        // Construct the safe view explicitly: no correctness, explanation, feedback or summary fields.
        return new Views.Story(s.id(), s.title(), s.scene(), s.introduction(), s.illustration(), s.resourceStatus(),
            s.reviewStatus(), s.contentSource(), s.syllabusVersion(), mapper.knowledgeIds(id),
            new Views.Question(q.id(), q.prompt(), options), 1);
    }
    public Views.Answer answer(long storyId, AnswerRequest request) {
        requireStory(storyId);
        Rows.Question question = singleQuestion(storyId);
        if (question.id() != request.questionId()) throw BusinessException.invalid("题目不属于当前故事");
        List<Rows.Option> options = mapper.options(question.id());
        Rows.Option selected = options.stream().filter(o -> o.id() == request.optionId()).findFirst()
            .orElseThrow(() -> BusinessException.invalid("选项不属于当前题目"));
        List<Views.Explanation> explanations = options.stream().map(o -> new Views.Explanation(o.id(), o.label(), o.name(),
            o.term(), o.plainMeaning(), o.isCorrect(), o.reason(), o.misconception())).toList();
        List<Views.Summary> summaries = mapper.knowledgeIds(storyId).stream().map(this::requireKnowledge)
            .filter(k -> accessible(k.resourceStatus(), k.reviewStatus())).map(k -> {
                Rows.Card card = mapper.cardForKnowledge(k.id());
                return new Views.Summary(k.id(), k.name(), k.definition(), k.plainExplanation(), k.keyConclusions(),
                    k.misconceptions(), card == null ? null : card.id());
            }).toList();
        return new Views.Answer(storyId, question.id(), selected.id(), selected.isCorrect(), selected.feedback(), explanations, summaries);
    }
    public Views.Card card(long id) {
        Rows.Card c = mapper.card(id);
        if (c == null) throw BusinessException.notFound();
        Rows.Knowledge k = requireKnowledge(c.knowledgeId());
        if (!accessible(k.resourceStatus(), k.reviewStatus())) throw BusinessException.notFound();
        return new Views.Card(c.id(), k.id(), k.name(), mapper.chapter(k.chapterId()).name(), k.definition(),
            k.plainExplanation(), k.keyConclusions(), k.misconceptions(), c.diagramType(), k.resourceStatus(),
            k.reviewStatus(), k.contentSource(), k.syllabusVersion(), links(k.id()));
    }
}
