package com.knowledge.realm.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    public BusinessException(HttpStatus status, String message) { super(message); this.status = status; }
    public HttpStatus status() { return status; }
    public static BusinessException notFound() { return new BusinessException(HttpStatus.NOT_FOUND, "内容不存在或尚未开放"); }
    public static BusinessException invalid(String message) { return new BusinessException(HttpStatus.BAD_REQUEST, message); }
}
