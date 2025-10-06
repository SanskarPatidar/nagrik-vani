package com.sih.AuthService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@Slf4j
public class RefreshTokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final Duration TOKEN_EXPIRY = Duration.ofDays(7); // ttl(Time To Live)

    public void storeRefreshToken(String userId, String deviceId, String token) {
        String key = "refresh:" + userId + ":" + deviceId;
        log.info(key);
        redisTemplate.opsForValue().set(key, token, TOKEN_EXPIRY); // not just set, but also set new expiry
    }

    public String getRefreshToken(String userId, String deviceId) {
        String key = "refresh:" + userId + ":" + deviceId;
        return redisTemplate.opsForValue().get(key); // May return null
    }

    public boolean isRefreshTokenValid(String userId, String deviceId, String providedToken) {
        String storedToken = getRefreshToken(userId, deviceId);
        return storedToken != null && storedToken.equals(providedToken);
    }

    public void deleteRefreshToken(String userId, String deviceId) {
        String key = "refresh:" + userId + ":" + deviceId;
        redisTemplate.delete(key);
    }

    public void clearAllCache() {
        // Blocking call if there are many keys
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("Error clearing Redis cache", e);
        }
    }
}
