package com.example.marketing_agency_app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "campaign_channel")
public class CampaignChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с кампанией
    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    // Связь с каналом
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "allocated_budget", precision = 12, scale = 2)
    private BigDecimal allocatedBudget;

    private Integer impressions = 0;
    private Integer clicks = 0;
    private Integer conversions = 0;

    @Column(name = "cost_per_click", precision = 12, scale = 2)
    private BigDecimal costPerClick;

    @Column(name = "cost_per_conversion", precision = 12, scale = 2)
    private BigDecimal costPerConversion;

    @Column(name = "click_through_rate", precision = 5, scale = 2)
    private BigDecimal clickThroughRate;

    @Column(name = "spent_amount", precision = 12, scale = 2)
    private BigDecimal spentAmount;

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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public BigDecimal getAllocatedBudget() {
        return allocatedBudget;
    }

    public void setAllocatedBudget(BigDecimal allocatedBudget) {
        this.allocatedBudget = allocatedBudget;
    }

    public Integer getImpressions() {
        return impressions;
    }

    public void setImpressions(Integer impressions) {
        this.impressions = impressions;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }

    public Integer getConversions() {
        return conversions;
    }

    public void setConversions(Integer conversions) {
        this.conversions = conversions;
    }

    public BigDecimal getCostPerClick() {
        return costPerClick;
    }

    public void setCostPerClick(BigDecimal costPerClick) {
        this.costPerClick = costPerClick;
    }

    public BigDecimal getCostPerConversion() {
        return costPerConversion;
    }

    public void setCostPerConversion(BigDecimal costPerConversion) {
        this.costPerConversion = costPerConversion;
    }

    public BigDecimal getClickThroughRate() {
        return clickThroughRate;
    }

    public void setClickThroughRate(BigDecimal clickThroughRate) {
        this.clickThroughRate = clickThroughRate;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }
}
