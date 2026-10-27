package com.knowledge.realm.common;

import com.knowledge.realm.vo.Views;

/** Optional learning extensions can observe an answer without coupling core HTTP code to AI. */
public record LearningAnswerSubmitted(Views.Answer answer) {}
