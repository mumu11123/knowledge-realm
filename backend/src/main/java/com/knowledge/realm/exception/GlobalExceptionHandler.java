package com.knowledge.realm.exception;

import com.knowledge.realm.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiResponse<Void>> business(BusinessException e) {
        return ResponseEntity.status(e.status()).body(ApiResponse.error(e.status().name(), e.getMessage()));
    }
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
        HandlerMethodValidationException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    ResponseEntity<ApiResponse<Void>> validation(Exception e) {
        return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_ARGUMENT", "参数无效，请检查题目和选项"));
    }
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> missing(Exception e) {
        return ResponseEntity.status(404).body(ApiResponse.error("NOT_FOUND", "内容不存在"));
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<Void>> method(Exception e) {
        return ResponseEntity.status(405).body(ApiResponse.error("METHOD_NOT_ALLOWED", "请求方式不支持"));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> unexpected(Exception e) {
        log.error("Request failed", e);
        return ResponseEntity.internalServerError().body(ApiResponse.error("INTERNAL_ERROR", "服务暂时不可用，请稍后重试"));
    }
}
