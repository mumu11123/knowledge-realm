package com.knowledge.realm.resources;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ResourceMapper {
    @Select("SELECT DISTINCT r.id,r.title,r.platform,r.author_name,r.author_url,r.url,r.description,r.content_review_status,r.link_verified_at FROM learning_resource r JOIN knowledge_resource kr ON r.id=kr.resource_id JOIN knowledge_point k ON kr.knowledge_id=k.id JOIN chapter c ON k.chapter_id=c.id WHERE c.subject_id=#{subjectId} ORDER BY r.id")
    List<ResourceViews.Row> forSubject(long subjectId);
    @Select("SELECT kr.resource_id,kr.knowledge_id FROM knowledge_resource kr JOIN knowledge_point k ON kr.knowledge_id=k.id JOIN chapter c ON k.chapter_id=c.id WHERE c.subject_id=#{subjectId} ORDER BY kr.sort_order,kr.resource_id")
    List<ResourceViews.Link> links(long subjectId);
}
