package com.sih.AuthService.repository;

import com.sih.AuthService.model.ConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigEntityRepository extends MongoRepository<ConfigEntity, String> {
}
