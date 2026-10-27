package com.knowledge.realm.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "realm.ai")
public class AiProperties {
    private boolean enabled;
    private String apiKey = "";
    private String baseUrl = "https://api.deepseek.com";
    private String model = "deepseek-flash";
    private int timeoutSeconds = 45;
    private String adminToken = "";
    private boolean cookieSecure;
    public boolean configured() { return enabled && !apiKey.isBlank(); }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String value) { apiKey = value; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String value) { baseUrl = value; }
    public String getModel() { return model; }
    public void setModel(String value) { model = value; }
    public int getTimeoutSeconds() { return Math.max(2, Math.min(timeoutSeconds, 90)); }
    public void setTimeoutSeconds(int value) { timeoutSeconds = value; }
    public String getAdminToken() { return adminToken; }
    public void setAdminToken(String value) { adminToken = value; }
    public boolean isCookieSecure() { return cookieSecure; }
    public void setCookieSecure(boolean value) { cookieSecure = value; }
}
