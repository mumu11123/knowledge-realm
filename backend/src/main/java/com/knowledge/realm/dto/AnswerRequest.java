package com.knowledge.realm.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AnswerRequest(@NotNull @Positive Long questionId, @NotNull @Positive Long optionId) {}
