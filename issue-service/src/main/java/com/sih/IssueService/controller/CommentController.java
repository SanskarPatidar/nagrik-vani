package com.sih.IssueService.controller;

import com.sanskar.sih.issue.CommentRequestDTO;
import com.sanskar.sih.issue.CommentResponseDTO;
import com.sih.IssueService.dto.PageResponse;
import com.sih.IssueService.model.Comment;
import com.sih.IssueService.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/issue")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{issueId}/comment/all")
    public ResponseEntity<PageResponse<CommentResponseDTO>> getCommentsForIssue(
            @PathVariable String issueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(commentService.getCommentsForIssue(issueId, PageRequest.of(page, size))));
    }

    @PutMapping("/comment/post")
    public ResponseEntity<CommentResponseDTO> postComment(
            @RequestBody CommentRequestDTO request,
            @RequestHeader("x-user-id") String userId
    ) {
        return ResponseEntity.ok(commentService.postComment(request, userId));
    }

}
