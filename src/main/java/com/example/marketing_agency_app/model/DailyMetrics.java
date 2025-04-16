package com.example.marketing_agency_app.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Immutable
@Table(name = "daily_metrics") // имя представления в базе данных
public class DailyMetrics {

    @Id
    @Column(name = "day")
    private LocalDate day;

    @Column(name = "daily_impressions")
    private Long dailyImpressions;

    @Column(name = "daily_clicks")
    private Long dailyClicks;

    @Column(name = "daily_conversions")
    private Long dailyConversions;

    @Column(name = "daily_spent", precision = 12, scale = 2)
    private BigDecimal dailySpent;

    // Геттеры и сеттеры

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Long getDailyImpressions() {
        return dailyImpressions;
    }

    public void setDailyImpressions(Long dailyImpressions) {
        this.dailyImpressions = dailyImpressions;
    }

    public Long getDailyClicks() {
        return dailyClicks;
    }

    public void setDailyClicks(Long dailyClicks) {
        this.dailyClicks = dailyClicks;
    }

    public Long getDailyConversions() {
        return dailyConversions;
    }

    public void setDailyConversions(Long dailyConversions) {
        this.dailyConversions = dailyConversions;
    }

    public BigDecimal getDailySpent() {
        return dailySpent;
    }

    public void setDailySpent(BigDecimal dailySpent) {
        this.dailySpent = dailySpent;
    }
}
