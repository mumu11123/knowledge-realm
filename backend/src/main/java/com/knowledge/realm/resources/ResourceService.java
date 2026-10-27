package com.knowledge.realm.resources;

import com.knowledge.realm.exception.BusinessException;
import com.knowledge.realm.mapper.LearningMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
public class ResourceService {
    private final ResourceMapper resources;
    private final LearningMapper learning;
    public ResourceService(ResourceMapper resources, LearningMapper learning) {
        this.resources=resources; this.learning=learning;
    }
    public List<ResourceViews.Resource> forSubject(long subjectId) {
        if (learning.subject(subjectId)==null) throw BusinessException.notFound();
        var links=resources.links(subjectId).stream().collect(Collectors.groupingBy(ResourceViews.Link::resourceId,
            Collectors.mapping(ResourceViews.Link::knowledgeId,Collectors.toList())));
        return resources.forSubject(subjectId).stream().map(r -> new ResourceViews.Resource(r.id(),r.title(),r.platform(),
            r.authorName(),r.authorUrl(),r.url(),r.description(),r.contentReviewStatus(),r.linkVerifiedAt(),
            links.getOrDefault(r.id(),List.of()))).toList();
    }
    public List<ResourceViews.Resource> forKnowledge(long knowledgeId) {
        var point=learning.knowledge(knowledgeId);
        if (point==null) throw BusinessException.notFound();
        var chapter=learning.chapter(point.chapterId());
        return forSubject(chapter.subjectId()).stream().filter(r -> r.knowledgeIds().contains(knowledgeId)).toList();
    }
}
