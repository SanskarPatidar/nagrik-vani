package com.sih.CitizenService.model;
import com.sanskar.sih.citizen.CitizenProfileView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "citizen_profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CitizenProfile implements CitizenProfileView {
    @Id
    private String id;
    private String userId; // reference to User
    private String fullName; // real world name can not be unique, thus not same as username of User
    private String address;

    private String profileImageUrl;
    private Long totalComplaints;
    private Long totalComments;
    private Long totalFeedbacks;
    private Double communityScore;
}
