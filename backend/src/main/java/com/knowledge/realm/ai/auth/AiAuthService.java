package com.knowledge.realm.ai.auth;

import com.knowledge.realm.ai.*;
import com.knowledge.realm.exception.BusinessException;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.time.Duration;

@Service
public class AiAuthService {
    public static final String COOKIE = "realm_learner";
    private final AiMapper mapper;
    private final AiProperties properties;
    public AiAuthService(AiMapper mapper, AiProperties properties) { this.mapper=mapper; this.properties=properties; }
    public Optional<AiContracts.Session> current(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies()).filter(c -> COOKIE.equals(c.getName()))
            .map(Cookie::getValue).filter(v -> v.matches("[A-Za-z0-9_-]{43}"))
            .map(v -> mapper.session(hash(v))).filter(Objects::nonNull).findFirst();
    }
    public AiContracts.Session require(HttpServletRequest request) {
        return current(request).orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED,"学习会话已过期，请重新打开 AI 助手"));
    }
    public AiContracts.Session establish(HttpServletRequest request, HttpServletResponse response) {
        var existing=current(request);
        if (existing.isPresent()) return existing.get();
        byte[] bytes=new byte[32]; new SecureRandom().nextBytes(bytes);
        String token=Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        mapper.createSession(hash(token));
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(COOKIE,token).httpOnly(true)
            .secure(properties.isCookieSecure()).sameSite("Strict").path("/api").maxAge(Duration.ofDays(30)).build().toString());
        return mapper.session(hash(token));
    }
    public void requireAdmin(String authorization) {
        String expected=properties.getAdminToken();
        if (expected.length()<32) throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE,"管理员凭证尚未配置（至少 32 字符）");
        String actual=authorization!=null && authorization.startsWith("Bearer ")?authorization.substring(7):"";
        if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),actual.getBytes(StandardCharsets.UTF_8)))
            throw new BusinessException(HttpStatus.FORBIDDEN,"需要管理员权限");
    }
    @Transactional
    public void clear(HttpServletRequest request,HttpServletResponse response) {
        long learner=require(request).id();
        mapper.deleteAttempts(learner);mapper.anonymizeAudit(learner);mapper.deleteSession(learner);
        response.addHeader(HttpHeaders.SET_COOKIE,ResponseCookie.from(COOKIE,"").httpOnly(true).secure(properties.isCookieSecure())
            .sameSite("Strict").path("/api").maxAge(Duration.ZERO).build().toString());
    }
    private static String hash(String token) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}
