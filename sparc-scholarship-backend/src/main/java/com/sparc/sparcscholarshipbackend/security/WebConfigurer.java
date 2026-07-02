package com.sparc.sparcscholarshipbackend.security;

import com.sparc.sparcscholarshipbackend.security.ratelimit.RateLimiterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    private final RateLimiterInterceptor rateLimiterInterceptor;

    public WebConfigurer(RateLimiterInterceptor rateLimiterInterceptor) {
        this.rateLimiterInterceptor = rateLimiterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //protect submit button and the upload button
        registry.addInterceptor(rateLimiterInterceptor)
                .addPathPatterns("/api/applications/submit", "/api/documents/upload");
    }
}