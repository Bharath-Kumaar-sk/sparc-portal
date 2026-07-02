package com.sparc.sparcscholarshipbackend.security.ratelimit;

public class TokenBucket {
    private final double maxCapacity;
    private double currentCapacity;
    private long lastRefillTime;
    private final double refillRate;

    public TokenBucket(double maxCapacity, double currentCapacity, long lastRefillTime, double refillRate) {
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.lastRefillTime = lastRefillTime;
        this.refillRate = refillRate;
    }

    public synchronized boolean tryConsume() {
        long requestTime = System.nanoTime();
        refillBucket(requestTime);

        lastRefillTime = requestTime;
        if (currentCapacity >= 1) {
            currentCapacity--;
            return true;
        } else {
            return false;
        }
    }

    public void refillBucket(long requestTime) {
        long timeDiffNano = requestTime - this.lastRefillTime;
        double tokensToRefill = (timeDiffNano / 1e9) * refillRate;

        if (tokensToRefill + this.currentCapacity > maxCapacity) {
            this.currentCapacity = maxCapacity;
        } else {
            this.currentCapacity += tokensToRefill;
        }
    }
}