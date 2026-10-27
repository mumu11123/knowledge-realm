package com.knowledge.realm.controller;

import com.knowledge.realm.common.ApiResponse;
import com.knowledge.realm.dto.AnswerRequest;
import com.knowledge.realm.service.LearningService;
import com.knowledge.realm.vo.Views;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.knowledge.realm.common.LearningAnswerSubmitted;
import org.springframework.context.ApplicationEventPublisher;

@RestController
@RequestMapping("/api")
@Tag(name = "知境学习", description = "游客演示学习 API；进度保存在浏览器本地")
public class LearningController {
    private final LearningService learning;
    private final ApplicationEventPublisher events;
    public LearningController(LearningService learning, ApplicationEventPublisher events) {
        this.learning = learning; this.events=events;
    }
    @GetMapping("/subjects")
    @Operation(summary = "科目目录")
    public ApiResponse<List<Views.Subject>> subjects() { return ApiResponse.ok(learning.subjects()); }
    @GetMapping("/subjects/{id}/chapters")
    @Operation(summary = "科目章节，包含可选的父章节 ID")
    public ApiResponse<List<Views.Chapter>> chapters(@PathVariable @Positive long id) { return ApiResponse.ok(learning.chapters(id)); }
    @GetMapping("/chapters/{id}/knowledge-points")
    @Operation(summary = "知识目录与开放故事；不含答案和完整知识卡")
    public ApiResponse<List<Views.Knowledge>> points(@PathVariable @Positive long id) { return ApiResponse.ok(learning.knowledgeInChapter(id)); }
    @GetMapping("/knowledge-points/{id}")
    @Operation(summary = "知识点简介、关联与来源")
    public ApiResponse<Views.Knowledge> knowledge(@PathVariable @Positive long id) { return ApiResponse.ok(learning.knowledge(id)); }
    @GetMapping("/stories/{id}")
    @Operation(summary = "当前故事及公开选项，不含答案或后续反馈")
    public ApiResponse<Views.Story> story(@PathVariable @Positive long id) { return ApiResponse.ok(learning.story(id)); }
    @PostMapping("/stories/{id}/answer")
    @Operation(summary = "服务端判分，返回所选剧情反馈、全部选项解析和知识总结")
    public ApiResponse<Views.Answer> answer(@PathVariable @Positive long id, @Valid @RequestBody AnswerRequest request) {
        var answer=learning.answer(id,request);
        events.publishEvent(new LearningAnswerSubmitted(answer));
        return ApiResponse.ok(answer);
    }
    @GetMapping("/knowledge-cards/{id}")
    @Operation(summary = "知识卡与关联故事；游客卡片解锁是本地 UI 状态，并非授权边界")
    public ApiResponse<Views.Card> card(@PathVariable @Positive long id) { return ApiResponse.ok(learning.card(id)); }
}
