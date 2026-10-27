package com.knowledge.realm.mapper;

import com.knowledge.realm.entity.Rows;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface LearningMapper {
    @Select("SELECT id,code,name,description,availability FROM subject ORDER BY sort_order")
    List<Rows.Subject> subjects();
    @Select("SELECT id,code,name,description,availability FROM subject WHERE id=#{id}")
    Rows.Subject subject(long id);
    @Select("SELECT id,subject_id,parent_id,name,sort_order FROM chapter WHERE subject_id=#{id} ORDER BY sort_order,id")
    List<Rows.Chapter> chapters(long id);
    @Select("SELECT id,subject_id,parent_id,name,sort_order FROM chapter WHERE id=#{id}")
    Rows.Chapter chapter(long id);
    @Select("SELECT id,chapter_id,name,introduction,definition,plain_explanation,key_conclusions,misconceptions,resource_status,review_status,content_source,syllabus_version FROM knowledge_point WHERE chapter_id=#{id} ORDER BY id")
    List<Rows.Knowledge> knowledgeInChapter(long id);
    @Select("SELECT id,chapter_id,name,introduction,definition,plain_explanation,key_conclusions,misconceptions,resource_status,review_status,content_source,syllabus_version FROM knowledge_point WHERE id=#{id}")
    Rows.Knowledge knowledge(long id);
    @Select("SELECT related_id FROM knowledge_relation WHERE knowledge_id=#{id} AND relation_type='PREREQUISITE' ORDER BY related_id")
    List<Long> prerequisites(long id);
    @Select("SELECT related_id FROM knowledge_relation WHERE knowledge_id=#{id} AND relation_type='RELATED' ORDER BY related_id")
    List<Long> related(long id);
    @Select("SELECT s.id,s.title,s.scene,s.introduction,s.illustration,s.resource_status,s.review_status,s.content_source,s.syllabus_version FROM story s JOIN story_knowledge sk ON s.id=sk.story_id WHERE sk.knowledge_id=#{id} AND (s.resource_status='DEMO' OR (s.resource_status='PUBLISHED' AND s.review_status='REVIEWED')) ORDER BY s.id")
    List<Rows.Story> storiesForKnowledge(long id);
    @Select("SELECT id,title,scene,introduction,illustration,resource_status,review_status,content_source,syllabus_version FROM story WHERE id=#{id}")
    Rows.Story story(long id);
    @Select("SELECT knowledge_id FROM story_knowledge WHERE story_id=#{id} ORDER BY knowledge_id")
    List<Long> knowledgeIds(long id);
    @Select("SELECT id,story_id,prompt,sort_order FROM question WHERE story_id=#{id} ORDER BY sort_order")
    List<Rows.Question> questions(long id);
    @Select("SELECT id,question_id,label,name,term,plain_meaning,is_correct,reason,misconception,feedback FROM question_option WHERE question_id=#{id} ORDER BY label")
    List<Rows.Option> options(long id);
    @Select("SELECT id,knowledge_id,diagram_type FROM knowledge_card WHERE id=#{id}")
    Rows.Card card(long id);
    @Select("SELECT id,knowledge_id,diagram_type FROM knowledge_card WHERE knowledge_id=#{id}")
    Rows.Card cardForKnowledge(long id);
}
