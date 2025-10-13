package com.sih.AuthService.repository;


import com.sih.AuthService.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String>, TokenRepositoryCustom {
    void deleteAllByExpiredTrueAndRevokedTrue();
    Optional<Token> findByToken(String token); // Find token by token(String)
}

interface TokenRepositoryCustom {
    List<Token> findValidTokensByUserIdAndDeviceId(String userId, String deviceId);
}

@Repository
@RequiredArgsConstructor
class TokenRepositoryCustomImpl implements TokenRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public List<Token> findValidTokensByUserIdAndDeviceId(String userId, String deviceId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .and("deviceId").is(deviceId)
                .and("revoked").is(false)
                .and("expired").is(false));

        return mongoTemplate.find(query, Token.class);
    }
}