package com.sih.ComplaintService.repository;

import com.sih.ComplaintService.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    Optional<Feedback> findByComplaintIdAndResolvementReportId(String complaintId, String resolvementReportId);
}
