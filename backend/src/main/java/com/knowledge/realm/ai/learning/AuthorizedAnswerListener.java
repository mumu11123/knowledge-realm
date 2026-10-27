package com.knowledge.realm.ai.learning;

import com.knowledge.realm.ai.auth.AiAuthService;
import com.knowledge.realm.common.LearningAnswerSubmitted;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthorizedAnswerListener {
    private final AiAuthService auth;
    private final AuthorizedLearningService records;
    public AuthorizedAnswerListener(AiAuthService auth, AuthorizedLearningService records) {
        this.auth=auth; this.records=records;
    }
    // Synchronous: resolve identity on the original authorized request thread, never from model arguments.
    @EventListener
    public void record(LearningAnswerSubmitted event) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes request) {
            auth.current(request.getRequest()).ifPresent(session -> records.record(session.id(),event.answer()));
        }
    }
}
