package com.akshat.urlShortner.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public String get(String key) {
        return Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString();
    }

    public void set(String key, String longUrl, Long ttl) {
        redisTemplate.opsForValue().set(key, longUrl, ttl, TimeUnit.SECONDS);
        return;
    }
}
