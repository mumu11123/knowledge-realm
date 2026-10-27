package com.knowledge.realm.ai;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public final class AiContracts {
    private AiContracts() {}
    public record Ask(@Positive long knowledgeId, @NotBlank @Size(max=1000) String message) {}
    public record Chunk(long id, long knowledgeId, String title, String body, String sourceUrl,
        String sourceTitle, String sourceVersion, String reviewStatus, String reviewedBy, LocalDateTime reviewedAt) {}
    public record Source(String id, long knowledgeId, String title, String excerpt, String url, String version) {}
    public record Citation(String sourceId, String quote) {}
    public record Claim(String text, String sourceId, String quote) {}
    public record ModelAnswer(boolean insufficient, List<Claim> claims) {}
    public record ToolEvent(String name, String status) {}
    public record TutorReply(String requestId, String status, String message, List<Claim> claims,
        List<Source> sources, List<ToolEvent> tools, java.util.Map<String,Object> toolResults, long elapsedMs, int promptTokens, int completionTokens) {}
    public record Session(long id, LocalDateTime expiresAt) {}
    public record Progress(long attempts, long correctAttempts, long storiesTouched) {}
    public record WrongPoint(long knowledgeId, String name, long wrongAttempts) {}
    public record ReviewItem(long knowledgeId, String name, String recommendation) {}
    public record Review(@NotBlank @Size(max=100) String reviewer, @NotBlank @Size(max=1000) String note, boolean approve) {}
    public record DraftRequest(@Positive long knowledgeId, @NotBlank @Size(max=300) String constraints,
        @NotBlank @Size(max=50) String difficulty, @NotBlank @Size(max=100) String style) {}
    public record DraftOption(String label, String name, String term, String plainMeaning, boolean correct,
        String reason, String misconception, String feedback) {}
    public record DraftContent(String title, String scene, String introduction, String prompt,
        List<DraftOption> options, List<Long> knowledgeIds, List<Citation> evidence) {}
    public record Draft(long id, long knowledgeId, String payload, String sourceIds, String status,
        String generatedBy, String reviewedBy, Long publishedStoryId) {}
}
