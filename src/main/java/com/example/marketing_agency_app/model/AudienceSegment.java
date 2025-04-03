package com.example.marketing_agency_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "audiencesegment")
public class AudienceSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audience_id")
    private Long audienceId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Можно хранить демографические данные в виде строки (JSON) или использовать специальный тип
    @Column(columnDefinition = "TEXT")
    private String demographics;

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

    public String getDemographics() {
        return demographics;
    }

    public void setDemographics(String demographics) {
        this.demographics = demographics;
    }
}
