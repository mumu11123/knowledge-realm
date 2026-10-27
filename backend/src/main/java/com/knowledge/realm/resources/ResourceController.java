package com.knowledge.realm.resources;

import com.knowledge.realm.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ResourceController {
    private final ResourceService service;
    public ResourceController(ResourceService service) { this.service=service; }
    @GetMapping("/subjects/{id}/resources")
    @Operation(summary="科目的外部拓展资源；不表示站内课程开放或内容通过审核")
    public ApiResponse<List<ResourceViews.Resource>> subject(@PathVariable @Positive long id) {
        return ApiResponse.ok(service.forSubject(id));
    }
    @GetMapping("/knowledge-points/{id}/resources")
    @Operation(summary="知识点相关视频，标明原作者与原站链接")
    public ApiResponse<List<ResourceViews.Resource>> knowledge(@PathVariable @Positive long id) {
        return ApiResponse.ok(service.forKnowledge(id));
    }
}
