package com.sih.AuthService.repository.token;


import com.sih.AuthService.model.Token;

import java.util.List;

public interface TokenRepositoryCustom {
    List<Token> findValidTokensByUserIdAndDeviceId(String userId, String deviceId);
}