package com.knowledge.realm.vo;

import java.util.List;

/** Explicit HTTP contracts separate public questions from post-submission explanations. */
public final class Views {
    private Views() {}
    public record Subject(long id, String code, String name, String description, String availability) {}
    public record Chapter(long id, long subjectId, Long parentId, String name, int sortOrder) {}
    public record StoryLink(long id, String title) {}
    public record Knowledge(long id, long chapterId, String name, String introduction, String resourceStatus,
        String reviewStatus, String contentSource, String syllabusVersion, List<Long> prerequisites,
        List<Long> relatedKnowledge, List<StoryLink> stories) {}
    public record Option(long id, String label, String name) {}
    public record Question(long id, String prompt, List<Option> options) {}
    public record Story(long id, String title, String scene, String introduction, String illustration,
        String resourceStatus, String reviewStatus, String contentSource, String syllabusVersion,
        List<Long> knowledgeIds, Question question, int totalQuestions) {}
    public record Explanation(long id, String label, String name, String term, String plainMeaning,
        boolean correct, String reason, String misconception) {}
    public record Summary(long knowledgeId, String name, String definition, String plainExplanation,
        String keyConclusions, String misconceptions, Long cardId) {}
    public record Answer(long storyId, long questionId, long selectedOptionId, boolean correct,
        String feedback, List<Explanation> explanations, List<Summary> summaries) {}
    public record Card(long id, long knowledgeId, String name, String chapterName, String definition,
        String plainExplanation, String keyConclusions, String misconceptions, String diagramType,
        String resourceStatus, String reviewStatus, String contentSource, String syllabusVersion,
        List<StoryLink> stories) {}
}
