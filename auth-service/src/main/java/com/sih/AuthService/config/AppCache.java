package com.sih.AuthService.config;

import com.sih.AuthService.repository.ConfigEntityRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppCache {

    public enum CacheKeys {
        SECRET_KEY
    }

    public Map<String, String> appCacheValues = new HashMap<>();

    @Autowired
    private ConfigEntityRepository configEntityRepository;

    @PostConstruct
    public void init() {
        loadSecretsFromDB();
    }

    public void loadSecretsFromDB() {
        appCacheValues.clear(); // reset
        configEntityRepository.findAll().forEach(configEntity ->
                appCacheValues.put(configEntity.getKey(), configEntity.getValue())
        );
    }

    public Map<String, String> getAll() {
        return new HashMap<>(appCacheValues);
    }
}