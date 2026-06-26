package com.help.desk.radis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Autowired
    private  RedisTemplate<String, Integer> redisTemplate;

    public boolean isAllowed(String key, int limit, int windowInSeconds) {

        ValueOperations<String, Integer> ops = redisTemplate.opsForValue();

        Long count = ops.increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowInSeconds));
        }

        return count <= limit;
    }
}
