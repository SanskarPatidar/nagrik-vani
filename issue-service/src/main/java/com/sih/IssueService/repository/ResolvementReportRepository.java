package com.sih.IssueService.repository;

import com.sih.IssueService.model.ResolvementReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResolvementReportRepository extends MongoRepository<ResolvementReport, String> {

}
