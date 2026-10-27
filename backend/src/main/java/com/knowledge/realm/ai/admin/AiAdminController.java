package com.knowledge.realm.ai.admin;

import com.knowledge.realm.ai.*;
import com.knowledge.realm.ai.auth.AiAuthService;
import com.knowledge.realm.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ai")
public class AiAdminController {
    private final AiAuthService auth; private final AiAdminService service;
    private final AiMapper mapper;
    public AiAdminController(AiAuthService auth,AiAdminService service,AiMapper mapper) { this.auth=auth; this.service=service; this.mapper=mapper; }
    @GetMapping("/observations") public ApiResponse<?> observations(@RequestHeader(value="Authorization",required=false) String header) {
        auth.requireAdmin(header); return ApiResponse.ok(mapper.observations());
    }
    @GetMapping("/chunks") public ApiResponse<?> chunks(@RequestHeader(value="Authorization",required=false) String header) {
        auth.requireAdmin(header); return ApiResponse.ok(service.chunks());
    }
    @PostMapping("/chunks/{id}/review") public ApiResponse<?> review(@PathVariable long id,@RequestHeader(value="Authorization",required=false) String header,@Valid @RequestBody AiContracts.Review review) {
        auth.requireAdmin(header); service.review(id,review); return ApiResponse.ok(Map.of("reviewed",true));
    }
    @GetMapping("/drafts") public ApiResponse<?> drafts(@RequestHeader(value="Authorization",required=false) String header) {
        auth.requireAdmin(header); return ApiResponse.ok(service.drafts());
    }
    @PostMapping("/drafts") public ApiResponse<?> generate(@RequestHeader(value="Authorization",required=false) String header,@Valid @RequestBody AiContracts.DraftRequest request) {
        auth.requireAdmin(header); return ApiResponse.ok(service.generate(request));
    }
    @PostMapping("/drafts/{id}/publish") public ApiResponse<?> publish(@PathVariable long id,@RequestHeader(value="Authorization",required=false) String header,@Valid @RequestBody AiContracts.Review review) {
        auth.requireAdmin(header); return ApiResponse.ok(Map.of("storyId",service.publish(id,review)));
    }
}
