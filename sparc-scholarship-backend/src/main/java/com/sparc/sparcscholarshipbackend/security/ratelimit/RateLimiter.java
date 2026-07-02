package com.sparc.sparcscholarshipbackend.security.ratelimit;

public interface RateLimiter {
    boolean allowRequest(String Ip);
}