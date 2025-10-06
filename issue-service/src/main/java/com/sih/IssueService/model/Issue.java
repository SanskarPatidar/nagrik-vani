package com.sih.IssueService.model;

import com.sanskar.sih.issue.IssueStatus;
import com.sanskar.sih.issue.IssueView;
import com.sanskar.sih.issue.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "issues")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Issue implements IssueView {

    @Id
    private String id;
    private String assignedToId; // reference to AuthorityProfile based on type of complaints
    private LocalDateTime issuedAt;

    private String title;
    private String description;
    private String imageUrl;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;
    private String city;
    private String state;
    private String country;
    private String type;
    private String priority; // here aging will be applied
    private IssueStatus status;
    private Long likes;

    @Override
    public Point getLocation() {
        return new Point(location.getY(), location.getX());
        // returning GeoJsonPoint as Point object
    }
}
