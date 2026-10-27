package com.knowledge.realm.resources;

import java.time.LocalDateTime;
import java.util.List;

public final class ResourceViews {
    private ResourceViews() {}
    public record Row(long id, String title, String platform, String authorName, String authorUrl,
        String url, String description, String contentReviewStatus, LocalDateTime linkVerifiedAt) {}
    public record Link(long resourceId, long knowledgeId) {}
    public record Resource(long id, String title, String platform, String authorName, String authorUrl,
        String url, String description, String contentReviewStatus, LocalDateTime linkVerifiedAt,
        List<Long> knowledgeIds) {}
}
