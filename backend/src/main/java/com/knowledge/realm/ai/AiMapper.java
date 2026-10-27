package com.knowledge.realm.ai;

import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AiMapper {
    @Insert("INSERT INTO learner_session(token_hash,expires_at) VALUES(#{hash},DATE_ADD(CURRENT_TIMESTAMP,INTERVAL 30 DAY))")
    void createSession(String hash);
    @Select("SELECT id,expires_at FROM learner_session WHERE token_hash=#{hash} AND expires_at>CURRENT_TIMESTAMP")
    AiContracts.Session session(String hash);
    @Delete("DELETE FROM learner_attempt WHERE learner_id=#{learner}") void deleteAttempts(long learner);
    @Update("UPDATE ai_call_log SET learner_id=NULL WHERE learner_id=#{learner}") void anonymizeAudit(long learner);
    @Delete("DELETE FROM learner_session WHERE id=#{learner}") void deleteSession(long learner);
    @Insert("INSERT INTO learner_attempt(learner_id,story_id,question_id,option_id,is_correct) VALUES(#{learner},#{story},#{question},#{option},#{correct})")
    void attempt(long learner, long story, long question, long option, boolean correct);
    @Select("SELECT COUNT(*) attempts,COALESCE(SUM(is_correct),0) correct_attempts,COUNT(DISTINCT story_id) stories_touched FROM learner_attempt WHERE learner_id=#{learner}")
    AiContracts.Progress progress(long learner);
    @Select("SELECT k.id knowledge_id,k.name,COUNT(*) wrong_attempts FROM learner_attempt a JOIN story_knowledge sk ON a.story_id=sk.story_id JOIN knowledge_point k ON sk.knowledge_id=k.id WHERE a.learner_id=#{learner} AND a.is_correct=FALSE GROUP BY k.id,k.name ORDER BY wrong_attempts DESC,k.id LIMIT 20")
    List<AiContracts.WrongPoint> wrong(long learner);
    @Select("<script>SELECT id,knowledge_id,title,body,source_url,source_title,source_version,review_status,reviewed_by,reviewed_at FROM ai_knowledge_chunk WHERE review_status='REVIEWED' AND knowledge_id IN <foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach> ORDER BY id LIMIT 200</script>")
    List<AiContracts.Chunk> reviewedChunks(@Param("ids") java.util.Set<Long> ids);
    @Select("SELECT id,knowledge_id,title,body,source_url,source_title,source_version,review_status,reviewed_by,reviewed_at FROM ai_knowledge_chunk ORDER BY id")
    List<AiContracts.Chunk> allChunks();
    @Update("UPDATE ai_knowledge_chunk SET review_status=#{status},reviewed_by=#{reviewer},review_note=#{note},source_version=REPLACE(source_version,'中文笔记待人工审核','中文笔记 v1'),reviewed_at=CURRENT_TIMESTAMP WHERE id=#{id}")
    int reviewChunk(long id, String status, String reviewer, String note);
    @Select("SELECT COUNT(*) FROM ai_call_log WHERE learner_id=#{learner} AND created_at>DATE_SUB(CURRENT_TIMESTAMP,INTERVAL 1 MINUTE)")
    int recentCalls(long learner);
    @Insert("INSERT INTO ai_call_log(request_id,learner_id,operation,status,model,elapsed_ms,prompt_tokens,completion_tokens,tool_names,error_code) VALUES(#{requestId},#{learner},#{operation},#{status},#{model},#{elapsed},#{promptTokens},#{completionTokens},#{tools},#{error})")
    void audit(String requestId, Long learner, String operation, String status, String model, long elapsed,
        int promptTokens, int completionTokens, String tools, String error);
    @Insert("INSERT INTO ai_story_draft(knowledge_id,payload,source_ids,generated_by) VALUES(#{knowledge},#{payload},#{sources},'configured-admin')")
    void saveDraft(long knowledge, String payload, String sources);
    @Select("SELECT LAST_INSERT_ID()") long lastId();
    @Select("SELECT id,knowledge_id,payload,source_ids,status,generated_by,reviewed_by,published_story_id FROM ai_story_draft WHERE id=#{id}")
    AiContracts.Draft draft(long id);
    @Select("SELECT id,knowledge_id,payload,source_ids,status,generated_by,reviewed_by,published_story_id FROM ai_story_draft ORDER BY id DESC LIMIT 50")
    List<AiContracts.Draft> drafts();
    @Select("SELECT request_id,operation,status,model,elapsed_ms,prompt_tokens,completion_tokens,tool_names,error_code,created_at FROM ai_call_log ORDER BY created_at DESC LIMIT 50")
    List<java.util.Map<String,Object>> observations();
    @Update("UPDATE ai_story_draft SET status='PUBLISHED',reviewed_by=#{reviewer},reviewed_at=CURRENT_TIMESTAMP,published_story_id=#{story} WHERE id=#{id} AND status='DRAFT'")
    int published(long id, String reviewer, long story);
}
