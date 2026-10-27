package com.knowledge.realm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataAccessException;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:mysql://${DB_HOST:127.0.0.1}:${DB_PORT:3307}/knowledge_realm_test?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@EnabledIfEnvironmentVariable(named = "RUN_MYSQL_TESTS", matches = "true")
class ApiMySqlTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Test void catalogIsRealMigratedMySql() throws Exception {
        mvc.perform(get("/api/subjects")).andExpect(status().isOk()).andExpect(jsonPath("$.data",hasSize(4)))
            .andExpect(jsonPath("$.data[1].availability").value("COMING_SOON"));
        mvc.perform(get("/api/subjects/1/chapters")).andExpect(status().isOk()).andExpect(jsonPath("$.data",hasSize(7)));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM flyway_schema_history WHERE success=1",Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM knowledge_point",Integer.class)).isGreaterThan(25);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM story",Integer.class)).isEqualTo(3);
    }
    @Test void safeQuestionContractAndAllBranchesWork() throws Exception {
        int[] correct = {111,212,311};
        for (int story=1; story<=3; story++) {
            var json = mvc.perform(get("/api/stories/"+story)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.question.options",hasSize(4))).andReturn().getResponse().getContentAsString();
            assertThat(json).doesNotContain("correct","feedback","reason","summaries","misconception");
            for (int index=1; index<=4; index++) {
                int question=story*10+1, option=story*100+10+index;
                mvc.perform(post("/api/stories/"+story+"/answer").contentType("application/json")
                    .content("{\"questionId\":"+question+",\"optionId\":"+option+"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.data.correct").value(option==correct[story-1]))
                    .andExpect(jsonPath("$.data.feedback",not(emptyString())))
                    .andExpect(jsonPath("$.data.explanations",hasSize(4)))
                    .andExpect(jsonPath("$.data.explanations[0].reason",not(emptyString())))
                    .andExpect(jsonPath("$.data.summaries[0].cardId").value(story));
            }
        }
    }
    @Test void invalidRelationshipsAndMalformedRequestsAreRejected() throws Exception {
        mvc.perform(post("/api/stories/1/answer").contentType("application/json").content("{\"questionId\":21,\"optionId\":211}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/stories/1/answer").contentType("application/json").content("{\"questionId\":11,\"optionId\":211}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/stories/1/answer").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/stories/1/answer").contentType("application/json").content("{\"questionId\":-1,\"optionId\":111}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/stories/999")).andExpect(status().isNotFound());
        mvc.perform(get("/api/stories/0")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/stories/abc")).andExpect(status().isBadRequest());
    }
    @Test void chapterRelationsAndCardsHaveSources() throws Exception {
        mvc.perform(get("/api/knowledge-points/301")).andExpect(status().isOk())
            .andExpect(jsonPath("$.data.prerequisites[0]").value(202)).andExpect(jsonPath("$.data.stories[0].id").value(2));
        mvc.perform(get("/api/knowledge-cards/1")).andExpect(status().isOk())
            .andExpect(jsonPath("$.data.definition",not(emptyString()))).andExpect(jsonPath("$.data.reviewStatus").value("DRAFT"));
        mvc.perform(get("/api/knowledge-cards/999")).andExpect(status().isNotFound());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk()).andExpect(jsonPath("$.openapi",startsWith("3.")));
    }
    @Test void databaseForbidsPublishingUnreviewedContent() {
        assertThatThrownBy(() -> jdbc.update("UPDATE story SET resource_status='PUBLISHED' WHERE id=1"))
            .isInstanceOf(DataAccessException.class).hasStackTraceContaining("ck_story_publication");
        assertThat(jdbc.queryForObject("SELECT resource_status FROM story WHERE id=1",String.class)).isEqualTo("DEMO");
    }
}
