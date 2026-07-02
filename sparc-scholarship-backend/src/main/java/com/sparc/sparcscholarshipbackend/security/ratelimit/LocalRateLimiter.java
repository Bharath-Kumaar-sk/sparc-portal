package com.sparc.sparcscholarshipbackend.security.ratelimit;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocalRateLimiter implements RateLimiter {
    private final int MAX_STRIKES = 5;
    private final long BAN_DURATION_MS = 30000L;

    private final double maxAmount;
    private final double refillRate;

    static class PenaltyClass {
        int strikes = 0;
        long banExpirationTime = 0;

        public PenaltyClass(int strikes, long banExpirationTime) {
            this.strikes = strikes;
            this.banExpirationTime = banExpirationTime;
        }
    }

    private final ConcurrentHashMap<String, PenaltyClass> IsBanned = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TokenBucket> map = new ConcurrentHashMap<>();

    public LocalRateLimiter(@Value("${maxAmount.amount:5.0}") double maxAmount,
                            @Value("${currAmount.amount:1.0}") double refillRate) {
        this.maxAmount = maxAmount;
        this.refillRate = refillRate;
    }

    @Override
    public boolean allowRequest(String Ip) {
        PenaltyClass penalty = IsBanned.computeIfAbsent(Ip, k -> new PenaltyClass(0, 0));

        if (penalty.banExpirationTime > System.currentTimeMillis()) {
            return false;
        } else if (penalty.banExpirationTime > 0) {
            penalty.strikes = 0;
            penalty.banExpirationTime = 0;
        }

        TokenBucket bucket = map.computeIfAbsent(Ip, k -> new TokenBucket(maxAmount, maxAmount, System.nanoTime(), refillRate));

        if (!bucket.tryConsume()) {
            penalty.strikes++;
            if (penalty.strikes >= MAX_STRIKES) {
                penalty.banExpirationTime = System.currentTimeMillis() + BAN_DURATION_MS;
            }
            return false;
        }
        return true;
    }
}