package com.sih.AuthService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder // so good
@NoArgsConstructor
@AllArgsConstructor
@Document("tokens")
public class Token {
    @Id
    private String id;
    @Indexed(unique = true)
    private String token;
    private boolean revoked;
    private boolean expired;
    private String userId;
    private String deviceId;
}