package com.knowledge.realm.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.realm.ai.admin.AiAdminService;
import com.knowledge.realm.ai.auth.AiAuthService;
import com.knowledge.realm.ai.knowledge.KnowledgeRetrievalService;
import com.knowledge.realm.ai.model.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties={
    "spring.datasource.url=jdbc:mysql://${DB_HOST:127.0.0.1}:${DB_PORT:3307}/knowledge_realm_test?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai",
    "realm.ai.enabled=false", "realm.ai.admin-token=test-admin-only-not-a-production-credential"})
@AutoConfigureMockMvc @ActiveProfiles("dev")
@EnabledIfEnvironmentVariable(named="RUN_MYSQL_TESTS",matches="true")
@Transactional
class AiApiMySqlTest {
    private static final String ADMIN="Bearer test-admin-only-not-a-production-credential";
    @Autowired MockMvc mvc; @Autowired JdbcTemplate jdbc; @Autowired AiMapper mapper;
    @Autowired KnowledgeRetrievalService retrieval; @Autowired AiAdminService admin; @Autowired ObjectMapper json;
    @Autowired LearningTools tools;
    @BeforeEach void isolateReviewState() {
        jdbc.update("UPDATE ai_knowledge_chunk SET review_status='PENDING' WHERE id BETWEEN 1 AND 4");
    }
    Cookie session() throws Exception {
        var response=mvc.perform(post("/api/ai/session")).andExpect(status().isOk()).andReturn().getResponse();
        assertThat(response.getHeader("Set-Cookie")).contains("HttpOnly","SameSite=Strict");
        return response.getCookie(AiAuthService.COOKIE);
    }
    @Test void browserSessionsCannotReadAnotherLearnersDataEvenWithForgedUserId() throws Exception {
        Cookie a=session(),b=session();
        mvc.perform(post("/api/stories/1/answer").cookie(a).contentType("application/json").content("{\"questionId\":11,\"optionId\":112}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.correct").value(false));
        mvc.perform(get("/api/ai/progress?learnerId=999&userId=999").cookie(a)).andExpect(status().isOk()).andExpect(jsonPath("$.data.attempts").value(1));
        mvc.perform(get("/api/ai/progress?learnerId=1").cookie(b)).andExpect(status().isOk()).andExpect(jsonPath("$.data.attempts").value(0));
        mvc.perform(get("/api/ai/progress")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/ai/tutor").contentType("application/json").content("{\"knowledgeId\":201,\"message\":\"链表\"}")).andExpect(status().isUnauthorized());
    }
    @Test void pendingAndRejectedNotesNeverEnterRagAndApprovedNotesHaveActualSources() throws Exception {
        Cookie cookie=session();
        assertThat(retrieval.retrieve(201,"单链表为什么不能随机访问")).isEmpty();
        mvc.perform(post("/api/admin/ai/chunks/1/review").header("Authorization",ADMIN).contentType("application/json")
            .content("{\"reviewer\":\"test-fixture\",\"note\":\"仅测试，不批准生产内容\",\"approve\":true}")).andExpect(status().isOk());
        assertThat(retrieval.retrieve(201,"单链表为什么不能随机访问")).extracting(AiContracts.Source::id).containsExactly("K1");
        assertThat(retrieval.retrieve(201,"今天东京天气如何")).isEmpty();
        mvc.perform(post("/api/ai/tutor").cookie(cookie).contentType("application/json").content("{\"knowledgeId\":201,\"message\":\"单链表为什么不能随机访问\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("MODEL_NOT_CONFIGURED"))
            .andExpect(jsonPath("$.data.claims",hasSize(0))).andExpect(jsonPath("$.data.sources[0].url",startsWith("https://opendsa.org/")));
        mvc.perform(post("/api/admin/ai/chunks/1/review").header("Authorization",ADMIN).contentType("application/json")
            .content("{\"reviewer\":\"test-fixture\",\"note\":\"测试撤销审核\",\"approve\":false}")).andExpect(status().isOk());
        assertThat(retrieval.retrieve(201,"随机访问链表")).isEmpty();
    }
    @Test void administratorsAreRequiredForAllContentOperations() throws Exception {
        Cookie cookie=session();
        mvc.perform(get("/api/admin/ai/chunks").cookie(cookie)).andExpect(status().isForbidden());
        mvc.perform(post("/api/admin/ai/chunks/1/review").cookie(cookie).contentType("application/json")
            .content("{\"reviewer\":\"fake\",\"note\":\"fake\",\"approve\":true}")).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/ai/drafts").header("Authorization","Bearer forged")).andExpect(status().isForbidden());
    }
    @Test void draftCanOnlyPublishAfterExplicitHumanApprovalAndCannotPublishTwice() throws Exception {
        jdbc.update("UPDATE ai_knowledge_chunk SET review_status='REVIEWED',reviewed_by='test-fixture',reviewed_at=CURRENT_TIMESTAMP WHERE id=1");
        var evidence=retrieval.approvedFor(201).getFirst();
        var options=java.util.stream.IntStream.range(0,4).mapToObj(i -> new AiContracts.DraftOption("ABCD".substring(i,i+1),"选项"+i,"术语","通俗解释",i==0,"独立原因","容易误解的地方","选择后的反馈")).toList();
        var content=new AiContracts.DraftContent("测试草稿","测试场景","只是测试开场","无歧义的测试问题",options,List.of(201L),List.of(new AiContracts.Citation(evidence.id(),evidence.excerpt().substring(0,12))));
        mapper.saveDraft(201,json.writeValueAsString(content),json.writeValueAsString(List.of(evidence.id())));long id=mapper.lastId();
        assertThat(mapper.draft(id).status()).isEqualTo("DRAFT");
        assertThatThrownBy(() -> admin.publish(id,new AiContracts.Review("test-fixture","未批准",false))).isInstanceOf(RuntimeException.class);
        long story=admin.publish(id,new AiContracts.Review("test-fixture","自动测试人工审批路径，不创建生产资料",true));
        assertThat(mapper.draft(id).status()).isEqualTo("PUBLISHED");
        mvc.perform(get("/api/stories/"+story)).andExpect(status().isOk()).andExpect(jsonPath("$.data.reviewStatus").value("REVIEWED"));
        assertThatThrownBy(() -> admin.publish(id,new AiContracts.Review("test-fixture","重复发布",true))).isInstanceOf(RuntimeException.class);
    }
    @Test void clearingOnlyCurrentSessionRevokesItsTokenAndKeepsOtherLearners() throws Exception {
        Cookie a=session(),b=session();
        mvc.perform(post("/api/stories/1/answer").cookie(a).contentType("application/json").content("{\"questionId\":11,\"optionId\":112}"));
        mvc.perform(delete("/api/ai/session").cookie(a)).andExpect(status().isOk());
        mvc.perform(get("/api/ai/progress").cookie(a)).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/ai/progress").cookie(b)).andExpect(status().isOk());
        mvc.perform(delete("/api/ai/session")).andExpect(status().isUnauthorized());
    }
    @Test void rateLimitAndInputLengthAreEnforced() throws Exception {
        Cookie cookie=session();
        Long learner=jdbc.queryForObject("SELECT MAX(id) FROM learner_session",Long.class);
        for(int i=0;i<10;i++) mapper.audit(UUID.randomUUID().toString(),learner,"TUTOR","TEST","test",0,0,0,"[]",null);
        mvc.perform(post("/api/ai/tutor").cookie(cookie).contentType("application/json").content("{\"knowledgeId\":201,\"message\":\"链表\"}")).andExpect(status().isTooManyRequests());
        mvc.perform(post("/api/ai/tutor").cookie(cookie).contentType("application/json").content("{\"knowledgeId\":201,\"message\":\""+"字".repeat(1001)+"\"}")).andExpect(status().isBadRequest());
    }
    @Test void retrievalEvaluationDatasetMeetsCoverageAndRefusalExpectations() throws Exception {
        jdbc.update("UPDATE ai_knowledge_chunk SET review_status='REVIEWED',reviewed_by='test-fixture',reviewed_at=CURRENT_TIMESTAMP");
        try(var stream=getClass().getResourceAsStream("/ai-rag-evaluation.json")) {
            var cases=json.readTree(stream);
            assertThat(cases.size()).isEqualTo(10);
            for(var item:cases) {
                var results=retrieval.retrieve(item.path("knowledgeId").asLong(),item.path("query").asText());
                if(item.path("refuse").asBoolean()) assertThat(results).as(item.path("id").asText()).isEmpty();
                else for(var expected:item.path("expectedSources")) assertThat(results).as(item.path("id").asText()).extracting(AiContracts.Source::id).contains(expected.asText());
            }
        }
    }
}
