package com.example.marketing_agency_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "campaign_audience")
public class CampaignAudience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с кампанией
    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    // Связь с сегментом аудитории
    @ManyToOne
    @JoinColumn(name = "audience_id", nullable = false)
    private AudienceSegment audience;

    // Getters и setters
    // ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public AudienceSegment getAudience() {
        return audience;
    }

    public void setAudience(AudienceSegment audience) {
        this.audience = audience;
    }
}

