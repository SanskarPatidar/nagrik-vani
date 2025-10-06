package com.sih.IssueService.model;

import com.sanskar.sih.issue.CommentView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements CommentView {
    @Id
    private String id;
    private String issueId; // reference to Issue
    private String citizenId;

    private String citizenName;
    private String content;
    private LocalDateTime timestamp;
}