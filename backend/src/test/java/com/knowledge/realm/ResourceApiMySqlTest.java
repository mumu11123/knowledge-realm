package com.knowledge.realm;

import com.knowledge.realm.ai.knowledge.KnowledgeRetrievalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties="spring.datasource.url=jdbc:mysql://${DB_HOST:127.0.0.1}:${DB_PORT:3307}/knowledge_realm_test?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
@AutoConfigureMockMvc @ActiveProfiles("dev")
@EnabledIfEnvironmentVariable(named="RUN_MYSQL_TESTS",matches="true")
class ResourceApiMySqlTest {
    @Autowired MockMvc mvc;
    @Autowired KnowledgeRetrievalService retrieval;
    @Test void verifiedCreatorLinksAreMappedToTheRightKnowledge() throws Exception {
        mvc.perform(get("/api/subjects/1/resources")).andExpect(status().isOk()).andExpect(jsonPath("$.data",hasSize(44)))
            .andExpect(jsonPath("$.data[*].authorName",everyItem(is("蓝不过海呀"))))
            .andExpect(jsonPath("$.data[*].authorUrl",everyItem(is("https://space.bilibili.com/401399175"))));
        mvc.perform(get("/api/knowledge-points/201/resources")).andExpect(status().isOk()).andExpect(jsonPath("$.data",hasSize(4)))
            .andExpect(jsonPath("$.data[0].url").value("https://www.bilibili.com/video/BV1CMQEBnE6o/"))
            .andExpect(jsonPath("$.data[0].knowledgeIds[0]").value(201))
            .andExpect(jsonPath("$.data[0].contentReviewStatus").value("NOT_REVIEWED"));
        mvc.perform(get("/api/knowledge-points/604/resources")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].title",containsString("散列表")));
    }
    @Test void externalVideosDoNotUnlockLessonsOrBecomeApprovedAiKnowledge() throws Exception {
        mvc.perform(get("/api/knowledge-points/602")).andExpect(status().isOk())
            .andExpect(jsonPath("$.data.resourceStatus").value("EMPTY")).andExpect(jsonPath("$.data.stories",hasSize(0)));
        mvc.perform(get("/api/knowledge-points/602/resources")).andExpect(status().isOk()).andExpect(jsonPath("$.data",hasSize(2)));
        assertThat(retrieval.retrieve(602,"二叉搜索树平衡二叉树")).isEmpty();
    }
    @Test void unavailableResourcesHaveExplicitEmptyOrErrorResponses() throws Exception {
        mvc.perform(get("/api/subjects/2/resources")).andExpect(status().isOk()).andExpect(jsonPath("$.data",hasSize(0)));
        mvc.perform(get("/api/knowledge-points/301/resources")).andExpect(status().isOk()).andExpect(jsonPath("$.data",hasSize(0)));
        mvc.perform(get("/api/subjects/999/resources")).andExpect(status().isNotFound());
        mvc.perform(get("/api/knowledge-points/999/resources")).andExpect(status().isNotFound());
        mvc.perform(get("/api/knowledge-points/0/resources")).andExpect(status().isBadRequest());
    }
}
