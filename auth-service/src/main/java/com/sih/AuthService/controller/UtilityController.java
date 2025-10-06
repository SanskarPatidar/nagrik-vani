package com.sih.AuthService.controller;


import com.sih.AuthService.config.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/util")
public class UtilityController {

    @Autowired
    private AppCache appCache;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cache/reload")
    public ResponseEntity<String> reloadCacheKeys() {
        appCache.loadSecretsFromDB();
        return ResponseEntity.ok("App cache reloaded.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cache/clear")
    public ResponseEntity<String> clearCacheKeys() {
        appCache.appCacheValues.clear();
        return ResponseEntity.ok("App cache cleared.");
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cache/all")
    public ResponseEntity<Map<String, String>> getAllCacheKeys() {
        return ResponseEntity.ok(appCache.getAll());
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}