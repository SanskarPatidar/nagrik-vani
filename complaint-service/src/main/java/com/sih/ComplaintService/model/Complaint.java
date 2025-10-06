package com.sih.ComplaintService.model;

import com.sanskar.sih.complaint.ComplaintStatus;
import com.sanskar.sih.complaint.ComplaintView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "complaints")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Complaint implements ComplaintView {
    @Id
    private String id;
    private String citizenId; // reference to CitizenProfile
    private String issueId; // reference to Issue to which this complaint is linked
    private LocalDateTime createdAt;

    private String title;
    private String description;
    private double locationLat;
    private double locationLon;
    private String city; // either from user input or reverse geocoding api
    private String state; // either from user input or reverse geocoding api
    private String country; // either from user input or reverse geocoding api
    private String type; // assigned by ai model
    private String priority; // Scale of 1-10 decided by ai model
    private ComplaintStatus complaintStatus;
    private boolean isPublic;
    private List<String> imageUrls;
}
