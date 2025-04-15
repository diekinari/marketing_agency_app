package com.example.marketing_agency_app.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "total_budget", precision = 12, scale = 2)
    private BigDecimal totalBudget;

    @Column(length = 50)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Связь с каналами (одна кампания может иметь множество записей в CampaignChannel)
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CampaignChannel> campaignChannels = new HashSet<>();

    // Связь с аудиториями (одна кампания может иметь множество записей в CampaignAudience)
    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CampaignAudience> campaignAudiences = new HashSet<>();


    @Transient
    private List<Long> audienceIds;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<CampaignChannel> getCampaignChannels() {
        return campaignChannels;
    }

    public void setCampaignChannels(Set<CampaignChannel> campaignChannels) {
        this.campaignChannels = campaignChannels;
    }

    public Set<CampaignAudience> getCampaignAudiences() {
        return campaignAudiences;
    }

    public void setCampaignAudiences(Set<CampaignAudience> campaignAudiences) {
        this.campaignAudiences = campaignAudiences;
    }

    public List<Long> getAudienceIds() {
        return audienceIds;
    }

    public void setAudienceIds(List<Long> audienceIds) {
        this.audienceIds = audienceIds;
    }
}

