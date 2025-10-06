package com.sih.IssueService.repository;

import com.sih.IssueService.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findAllCommentsByIssueId(String issueId, Pageable pageable);
}
