package com.sih.ComplaintService.repository;

import com.sanskar.sih.complaint.ComplaintStatus;
import com.sih.ComplaintService.model.Complaint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends MongoRepository<Complaint, String>, ComplaintRepositoryCustom {
    Page<Complaint> findAll(Pageable pageable);
    Page<Complaint> findAllByCitizenId(String id, Pageable pageable);
    Page<Complaint> findAllByIssueId(String issueId, Pageable pageable);
}

@Repository
interface ComplaintRepositoryCustom {
    public void acknowledgeComplaints(String issueId);
}

@Repository
class ComplaintRepositoryCustomImpl implements ComplaintRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void acknowledgeComplaints(String issueId) {
        Query query = new Query(Criteria.where("issueId").is(issueId));
        Update update = new Update().set("complaintStatus", ComplaintStatus.ACKNOWLEDGED); // mark as acknowledged
        mongoTemplate.updateMulti(query, update, "complaints"); // "complaints" = collection name
    }
}




