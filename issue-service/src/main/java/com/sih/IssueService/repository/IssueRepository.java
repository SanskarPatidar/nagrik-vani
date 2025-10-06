package com.sih.IssueService.repository;

import com.sanskar.sih.issue.IssueSearchRequestDTO;
import com.sih.IssueService.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends MongoRepository<Issue,String>, IssueRepositoryCustom {
    Page<Issue> findAll(Pageable pageable);
}

interface IssueRepositoryCustom {
    Page<Issue> searchIssues(IssueSearchRequestDTO searchRequest, Pageable pageable);
    Issue findByTypeAndWithinRadius(String type, double centerLat, double centerLon, double radiusInMeters);
}

@Repository
class IssueRepositoryCustomImpl implements IssueRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Issue> searchIssues(IssueSearchRequestDTO searchRequest, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (searchRequest.getId() != null) {
            criteriaList.add(Criteria.where("id").is(searchRequest.getId()));
        }
        if (searchRequest.getAssignedToId() != null) {
            criteriaList.add(Criteria.where("assignedTo").is(searchRequest.getAssignedToId()));
        }
        if (searchRequest.getTitle() != null) {
            criteriaList.add(Criteria.where("title").regex(searchRequest.getTitle(), "i")); // case insensitive matching
        }
        if (searchRequest.getCity() != null) {
            criteriaList.add(Criteria.where("city").is(searchRequest.getCity()));
        }
        if (searchRequest.getState() != null) {
            criteriaList.add(Criteria.where("state").is(searchRequest.getState()));
        }
        if (searchRequest.getCountry() != null) {
            criteriaList.add(Criteria.where("country").is(searchRequest.getCountry()));
        }
        if (searchRequest.getType() != null) {
            criteriaList.add(Criteria.where("type").is(searchRequest.getType()));
        }
        if (searchRequest.getPriority() != null) {
            criteriaList.add(Criteria.where("priority").is(searchRequest.getPriority()));
        }
        if (searchRequest.getStatus() != null) {
            criteriaList.add(Criteria.where("status").is(searchRequest.getStatus()));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // Add sorting
        if ("desc".equalsIgnoreCase(searchRequest.getOrder())) {
            query.with(pageable.getSort().descending());
        } else {
            query.with(pageable.getSort().ascending());
        }

        // Add pagination
        query.with(pageable);

        List<Issue> issues = mongoTemplate.find(query, Issue.class);
        long total = mongoTemplate.count(query.skip(-1).limit(-1), Issue.class);

        return new PageImpl<>(issues, pageable, total);
    }

    @Override
    public Issue findByTypeAndWithinRadius(String type, double centerLat, double centerLon, double radiusInMeters) {
        // GeoJSON Point uses (longitude, latitude)
        Point center = new Point(centerLon, centerLat);
        Distance distance = new Distance(radiusInMeters / 1000.0d, Metrics.KILOMETERS); // radius in km

        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type));
        query.addCriteria(Criteria.where("location").nearSphere(center).maxDistance(distance.getValue()));

        return mongoTemplate.findOne(query, Issue.class);
    }
}

