package com.knowledge.realm.ai.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledge.realm.ai.AiContracts;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class GroundedAnswerValidator {
    private final ObjectMapper json;
    public GroundedAnswerValidator(ObjectMapper json) { this.json=json; }
    public AiContracts.ModelAnswer validate(String output,List<AiContracts.Source> sources) throws Exception {
        AiContracts.ModelAnswer answer=json.readValue(cleanJson(output),AiContracts.ModelAnswer.class);
        if(answer.insufficient()) return new AiContracts.ModelAnswer(true,List.of());
        if(answer.claims()==null || answer.claims().isEmpty() || answer.claims().size()>8) throw new IllegalArgumentException("INVALID_EVIDENCE");
        for(var claim:answer.claims()) {
            var source=sources.stream().filter(s -> s.id().equals(claim.sourceId())).findFirst().orElseThrow(() -> new IllegalArgumentException("INVALID_EVIDENCE"));
            if(claim.text()==null || claim.text().isBlank() || claim.text().length()>1500 || claim.text().contains("://")
                || claim.quote()==null || claim.quote().length()<6 || !source.excerpt().contains(claim.quote()))
                throw new IllegalArgumentException("INVALID_EVIDENCE");
        }
        return answer;
    }
    public static String cleanJson(String output) {
        if(output==null || output.length()>30000) throw new IllegalArgumentException("INVALID_OUTPUT");
        String value=output.trim();
        if(value.startsWith("```")) { int start=value.indexOf('\n'); int end=value.lastIndexOf("```"); if(start<0 || end<=start) throw new IllegalArgumentException("INVALID_OUTPUT"); value=value.substring(start+1,end).trim(); }
        return value;
    }
}
