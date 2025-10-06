package com.sih.CitizenService.repository;

import com.sih.CitizenService.model.CitizenProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CitizenProfileRepository extends MongoRepository<CitizenProfile, String> {
    Optional<CitizenProfile> findByUserId(String userId);
}
