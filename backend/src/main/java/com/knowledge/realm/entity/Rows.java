package com.knowledge.realm.entity;

/** Persistence projections only. Never returned directly by controllers. */
public final class Rows {
    private Rows() {}
    public record Subject(long id, String code, String name, String description, String availability) {}
    public record Chapter(long id, long subjectId, Long parentId, String name, int sortOrder) {}
    public record Knowledge(long id, long chapterId, String name, String introduction, String definition,
        String plainExplanation, String keyConclusions, String misconceptions, String resourceStatus,
        String reviewStatus, String contentSource, String syllabusVersion) {}
    public record Story(long id, String title, String scene, String introduction, String illustration,
        String resourceStatus, String reviewStatus, String contentSource, String syllabusVersion) {}
    public record Question(long id, long storyId, String prompt, int sortOrder) {}
    public record Option(long id, long questionId, String label, String name, String term, String plainMeaning,
        boolean isCorrect, String reason, String misconception, String feedback) {}
    public record Card(long id, long knowledgeId, String diagramType) {}
}
