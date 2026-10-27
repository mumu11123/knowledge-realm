package com.knowledge.realm.ai.tutor;

import com.knowledge.realm.ai.*;
import com.knowledge.realm.ai.auth.AiAuthService;
import com.knowledge.realm.ai.learning.AuthorizedLearningService;
import com.knowledge.realm.ai.knowledge.KnowledgeRetrievalService;
import com.knowledge.realm.common.ApiResponse;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class TutorController {
    private final AiAuthService auth; private final TutorService tutor;
    private final AuthorizedLearningService learning; private final AiProperties properties;
    private final KnowledgeRetrievalService retrieval;
    public TutorController(AiAuthService auth,TutorService tutor,AuthorizedLearningService learning,AiProperties properties,KnowledgeRetrievalService retrieval) {
        this.auth=auth; this.tutor=tutor; this.learning=learning; this.properties=properties; this.retrieval=retrieval;
    }
    @PostMapping("/session") public ApiResponse<?> session(HttpServletRequest request,HttpServletResponse response) {
        var session=auth.establish(request,response);
        return ApiResponse.ok(Map.of("expiresAt",session.expiresAt(),"mode","AUTHORIZED_BROWSER_SESSION"));
    }
    @GetMapping("/status") public ApiResponse<?> status() { return ApiResponse.ok(Map.of("configured",properties.configured(),"model",properties.getModel())); }
    @DeleteMapping("/session") public ApiResponse<?> clear(HttpServletRequest request,HttpServletResponse response) {
        auth.clear(request,response); return ApiResponse.ok(Map.of("cleared",true));
    }
    @GetMapping("/progress") public ApiResponse<?> progress(HttpServletRequest request) { return ApiResponse.ok(learning.progress(auth.require(request).id())); }
    @GetMapping("/knowledge/{id}/sources") public ApiResponse<?> sources(@PathVariable long id,HttpServletRequest request) {
        auth.require(request); return ApiResponse.ok(retrieval.approvedFor(id));
    }
    @PostMapping("/tutor") public ApiResponse<AiContracts.TutorReply> ask(@Valid @RequestBody AiContracts.Ask request,HttpServletRequest http) {
        return ApiResponse.ok(tutor.ask(auth.require(http).id(),request));
    }
}
