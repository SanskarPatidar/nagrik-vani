package com.sih.AuthService.repository.token;

import com.sih.AuthService.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TokenRepositoryCustomImpl implements TokenRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

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
