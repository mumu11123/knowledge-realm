package com.knowledge.realm.ai.admin;

import com.knowledge.realm.ai.AiContracts;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DraftValidator {
    public void validate(AiContracts.DraftContent draft,long knowledgeId,List<AiContracts.Source> sources) {
        text(draft.title(),120); text(draft.scene(),150); text(draft.introduction(),3000); text(draft.prompt(),1000);
        if(draft.knowledgeIds()==null || !draft.knowledgeIds().equals(List.of(knowledgeId))) fail();
        if(draft.options()==null || draft.options().size()!=4 || draft.options().stream().filter(AiContracts.DraftOption::correct).count()!=1) fail();
        Set<String> labels=new HashSet<>();
        for(var option:draft.options()) {
            if(!Set.of("A","B","C","D").contains(option.label()) || !labels.add(option.label())) fail();
            text(option.name(),180); text(option.term(),100); text(option.plainMeaning(),1000);
            text(option.reason(),1500); text(option.misconception(),1000); text(option.feedback(),1500);
        }
        if(draft.evidence()==null || draft.evidence().isEmpty() || draft.evidence().size()>8) fail();
        for(var citation:draft.evidence()) {
            var source=sources.stream().filter(s -> s.id().equals(citation.sourceId())).findFirst().orElseThrow(() -> new IllegalArgumentException("INVALID_DRAFT_EVIDENCE"));
            if(citation.quote()==null || citation.quote().length()<6 || !source.excerpt().contains(citation.quote())) fail();
        }
    }
    private void text(String text,int max) { if(text==null || text.isBlank() || text.length()>max || text.contains("://")) fail(); }
    private void fail() { throw new IllegalArgumentException("INVALID_DRAFT"); }
}
