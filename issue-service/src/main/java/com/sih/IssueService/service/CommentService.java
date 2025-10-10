package com.sih.IssueService.service;

import com.sanskar.common.exception.FeignCallDelegation;
import com.sanskar.sih.citizen.CitizenProfileResponseDTO;
import com.sanskar.sih.issue.CommentRequestDTO;
import com.sanskar.sih.issue.CommentResponseDTO;
import com.sih.IssueService.client.CitizenClient;
import com.sih.IssueService.model.Comment;
import com.sih.IssueService.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // for final or @NonNull fields
public class CommentService {
    private final CitizenClient citizenClient;
    private final CommentRepository commentRepository;

    public Page<CommentResponseDTO> getCommentsForIssue(String issueId, Pageable pageable) {
        return commentRepository.findAllCommentsByIssueId(issueId, pageable)
                .map(CommentResponseDTO::new);
    }

    public CommentResponseDTO postComment(CommentRequestDTO request, String userId) {
        CitizenProfileResponseDTO citizenProfile = CitizenProfileResponseDTO.builder()
                .userId(userId)
                .totalComments(1L)
                .build();

        CitizenProfileResponseDTO updatedProfile = FeignCallDelegation.execute(
                () -> citizenClient.internalUpdateProfile(citizenProfile)
        );

        return new CommentResponseDTO(
                commentRepository.save(
                        Comment.builder()
                                .id(UUID.randomUUID().toString())
                                .content(request.getContent())
                                .timestamp(LocalDateTime.now())
                                .citizenId(updatedProfile.getId())
                                .citizenName(updatedProfile.getFullName())
                                .build()
                )
        );
    }
}
