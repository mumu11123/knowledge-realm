package com.knowledge.realm.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.realm.ai.model.GroundedAnswerValidator;
import com.knowledge.realm.ai.admin.DraftValidator;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class GroundedAnswerValidatorTest {
    static final AiContracts.Source SOURCE=new AiContracts.Source("K1",201,"来源","单链表的结点由指针链接，不能由下标直接计算任意结点地址。","https://opendsa.org/test","v1");
    final GroundedAnswerValidator validator=new GroundedAnswerValidator(new ObjectMapper());
    @Test void acceptsOnlyExistingSourceAndExactEvidence() throws Exception {
        var answer=validator.validate("{\"insufficient\":false,\"claims\":[{\"text\":\"需要沿链接寻找。\",\"sourceId\":\"K1\",\"quote\":\"单链表的结点由指针链接\"}]}",List.of(SOURCE));
        assertThat(answer.claims()).hasSize(1);
    }
    @Test void fabricatedSourceIsRejected() {
        assertThatThrownBy(() -> validator.validate("{\"insufficient\":false,\"claims\":[{\"text\":\"解释\",\"sourceId\":\"K999\",\"quote\":\"单链表的结点由指针链接\"}]}",List.of(SOURCE))).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void fabricatedQuoteIsRejected() {
        assertThatThrownBy(() -> validator.validate("{\"insufficient\":false,\"claims\":[{\"text\":\"解释\",\"sourceId\":\"K1\",\"quote\":\"单链表随机访问永远是常数时间\"}]}",List.of(SOURCE))).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void modelCannotInjectExternalCitationUrl() {
        assertThatThrownBy(() -> validator.validate("{\"insufficient\":false,\"claims\":[{\"text\":\"看 https://invented.invalid\",\"sourceId\":\"K1\",\"quote\":\"单链表的结点由指针链接\"}]}",List.of(SOURCE))).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void explicitInsufficiencyIsNotTurnedIntoAnAnswer() throws Exception {
        assertThat(validator.validate("{\"insufficient\":true,\"claims\":[]}",List.of()).insufficient()).isTrue();
    }
    @Test void multipleCorrectDraftOptionsAreRejected() {
        var option=new AiContracts.DraftOption("A","名称","术语","说明",true,"原因","误区","反馈");
        var invalid=new AiContracts.DraftContent("标题","场景","开场","问题",List.of(option,option,option,option),List.of(201L),List.of(new AiContracts.Citation("K1","单链表的结点由指针链接")));
        assertThatThrownBy(() -> new DraftValidator().validate(invalid,201,List.of(SOURCE))).isInstanceOf(IllegalArgumentException.class);
    }
}
