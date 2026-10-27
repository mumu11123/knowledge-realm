package com.knowledge.realm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.realm.dto.AnswerRequest;
import com.knowledge.realm.entity.Rows;
import com.knowledge.realm.exception.BusinessException;
import com.knowledge.realm.mapper.LearningMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningServiceTest {
    @Mock LearningMapper mapper;
    LearningService service;
    @BeforeEach void setup() { service = new LearningService(mapper); }
    void story() {
        when(mapper.story(1)).thenReturn(new Rows.Story(1,"书屋","傍晚","开场","library","DEMO","DRAFT","原创","演示"));
        when(mapper.questions(1)).thenReturn(List.of(new Rows.Question(11,1,"问题",1)));
    }
    List<Rows.Option> options() {
        return List.of(new Rows.Option(111,11,"A","顺序表","术语","解释",true,"正确原因","误区","正确反馈"),
            new Rows.Option(112,11,"B","单链表","术语","解释",false,"错误原因","误区","错误反馈"));
    }
    @Test void publicStoryDoesNotLeakAnyAnswerFields() throws Exception {
        story(); when(mapper.options(11)).thenReturn(options());
        String json = new ObjectMapper().writeValueAsString(service.story(1));
        assertThat(json).doesNotContain("isCorrect","correct","feedback","reason","misconception","plainMeaning","summaries","正确反馈","正确原因");
        assertThat(json).contains("顺序表","单链表");
    }
    @Test void rejectsQuestionFromAnotherStoryBeforeReadingOptions() {
        story();
        assertThatThrownBy(() -> service.answer(1,new AnswerRequest(21L,211L))).isInstanceOf(BusinessException.class).hasMessage("题目不属于当前故事");
        verify(mapper, never()).options(anyLong());
    }
    @Test void rejectsOptionFromAnotherQuestion() {
        story(); when(mapper.options(11)).thenReturn(options());
        assertThatThrownBy(() -> service.answer(1,new AnswerRequest(11L,211L))).isInstanceOf(BusinessException.class).hasMessage("选项不属于当前题目");
    }
    @Test void wrongAnswerStillReturnsEveryExplanation() {
        story(); when(mapper.options(11)).thenReturn(options());
        var answer = service.answer(1,new AnswerRequest(11L,112L));
        assertThat(answer.correct()).isFalse(); assertThat(answer.feedback()).isEqualTo("错误反馈");
        assertThat(answer.explanations()).hasSize(2); assertThat(answer.explanations().getFirst().correct()).isTrue();
    }
    @Test void scoresCorrectOptionOnServer() {
        story(); when(mapper.options(11)).thenReturn(options());
        var answer = service.answer(1,new AnswerRequest(11L,111L));
        assertThat(answer.correct()).isTrue(); assertThat(answer.feedback()).isEqualTo("正确反馈");
    }
    @Test void unpublishedDraftCannotBeOpenedOrAnswered() {
        when(mapper.story(1)).thenReturn(new Rows.Story(1,"草稿","场景","开场","library","PUBLISHED","DRAFT","来源","版本"));
        assertThatThrownBy(() -> service.story(1)).isInstanceOf(BusinessException.class);
        assertThatThrownBy(() -> service.answer(1,new AnswerRequest(11L,111L))).isInstanceOf(BusinessException.class);
    }
    @Test void rejectsUnavailableStory() {
        assertThatThrownBy(() -> service.story(99)).isInstanceOf(BusinessException.class);
    }
}
