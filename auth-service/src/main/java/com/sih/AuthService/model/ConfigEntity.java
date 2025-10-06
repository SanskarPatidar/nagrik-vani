package com.sih.AuthService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("config_entities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigEntity {
    @Id
    private String id;
    private String key;
    private String value;
}
