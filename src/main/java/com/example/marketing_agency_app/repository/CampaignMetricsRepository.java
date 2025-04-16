package com.example.marketing_agency_app.repository;

import com.example.marketing_agency_app.model.CampaignMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignMetricsRepository extends JpaRepository<CampaignMetrics, Long> {

    // Группировка по startDate: для каждой даты суммируем totalImpressions
    @Query("SELECT c.startDate, SUM(c.totalImpressions) " +
            "FROM CampaignMetrics c " +
            "WHERE c.startDate BETWEEN :startDate AND :endDate " +
            "GROUP BY c.startDate " +
            "ORDER BY c.startDate")
    List<Object[]> findImpressionsGroupedByDate(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // Новый метод для агрегации кликов (динамика кликов)
    @Query("SELECT c.startDate, SUM(c.totalClicks) " +
            "FROM CampaignMetrics c " +
            "WHERE c.startDate BETWEEN :startDate AND :endDate " +
            "GROUP BY c.startDate " +
            "ORDER BY c.startDate")
    List<Object[]> findClicksGroupedByDate(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}