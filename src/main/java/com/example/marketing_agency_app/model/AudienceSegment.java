package com.example.marketing_agency_app.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;


@Entity
@Table(name = "audiencesegment")
// Регистрируем поддержку jsonb через Hibernate Types 60
//@Convert(attributeName = "audienceSegment", converter = JsonBinaryType.class)
public class AudienceSegment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long audienceId;

    private String name;

    private String description;

    // Теперь поле — JsonNode, Hibernate Types сам сериализует/десериализует в jsonb
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode demographics;

    // Если необходимо, можно добавить обратную связь с CampaignAudience
    // ...

    // Getters и setters
    // ...

    public Long getAudienceId() {
        return audienceId;
    }

    public void setAudienceId(Long audienceId) {
        this.audienceId = audienceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JsonNode getDemographics() {
        return demographics;
    }

    public void setDemographics(JsonNode demographics) {
        this.demographics = demographics;
    }
}
