package com.example.marketing_agency_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_metrics")
@org.hibernate.annotations.Immutable
public class CampaignMetrics {

    @Id
    @Column(name = "campaign_id")
    private Long campaignId;

    private String name;
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_budget", precision = 12, scale = 2)
    private BigDecimal totalBudget;

    private String status;

    @Column(name = "total_impressions")
    private Long totalImpressions;

    @Column(name = "total_clicks")
    private Long totalClicks;

    @Column(name = "total_conversions")
    private Long totalConversions;

    @Column(name = "total_spent", precision = 12, scale = 2)
    private BigDecimal totalSpent;

    @Column(name = "roi_percentage", precision = 5, scale = 2)
    private BigDecimal roiPercentage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters только для чтения (сущность неизменяемая)

    public Long getCampaignId() {
        return campaignId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public String getStatus() {
        return status;
    }

    public Long getTotalImpressions() {
        return totalImpressions;
    }

    public Long getTotalClicks() {
        return totalClicks;
    }

    public Long getTotalConversions() {
        return totalConversions;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public BigDecimal getRoiPercentage() {
        return roiPercentage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

