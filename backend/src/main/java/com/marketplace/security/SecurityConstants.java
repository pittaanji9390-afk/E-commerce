package com.marketplace.security;

public final class SecurityConstants {

    private SecurityConstants() {}

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    public static final String REQUEST_ID_HEADER = "X-Request-ID";

    public static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**",
            "/api/v1/categories/**",
            "/api/v1/brands/**",
            "/api/v1/products/**",
            "/api/v1/search/**",
            "/api/v1/payments/webhook",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health/**",
            "/actuator/info"
    };
}
