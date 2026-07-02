package com.sparc.sparcscholarshipbackend.security.ratelimit;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiter rateLimiter;

    public RateLimiterInterceptor(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String Ip = request.getRemoteAddr();

        if (!rateLimiter.allowRequest(Ip)) {
            response.setStatus(429); // HTTP 429: Too Many Requests
            return false;
        }
        return true;
    }
}