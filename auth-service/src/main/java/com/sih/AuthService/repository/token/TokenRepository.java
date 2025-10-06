package com.sih.AuthService.repository.token;


import com.sih.AuthService.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String>, TokenRepositoryCustom {
    void deleteAllByExpiredTrueAndRevokedTrue();
    Optional<Token> findByToken(String token); // Find token by token(String)
}