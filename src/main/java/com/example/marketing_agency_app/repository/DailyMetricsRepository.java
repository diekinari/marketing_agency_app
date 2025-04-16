package com.example.marketing_agency_app.repository;

import com.example.marketing_agency_app.model.DailyMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface DailyMetricsRepository extends JpaRepository<DailyMetrics, Long> {

    // Этот метод возвращает список массивов, где:
    // [0] – дата (day),
    // [1] – daily_impressions,
    // [2] – daily_clicks,
    // [3] – daily_conversions,
    // [4] – daily_spent
    @Query(value = "SELECT gs::date AS day, " +
            "       SUM(cc.impressions) / COUNT(*) AS daily_impressions, " +
            "       SUM(cc.clicks) / COUNT(*) AS daily_clicks, " +
            "       SUM(cc.conversions) / COUNT(*) AS daily_conversions, " +
            "       SUM(cc.spent_amount) / COUNT(*) AS daily_spent " +
            "FROM campaign c " +
            "JOIN campaign_channel cc ON c.campaign_id = cc.campaign_id " +
            "CROSS JOIN LATERAL generate_series(c.start_date, c.end_date, interval '1 day') AS gs " +
            "WHERE gs::date BETWEEN :startDate AND :endDate " +
            "GROUP BY gs::date " +
            "ORDER BY gs::date",
            nativeQuery = true)
    List<Object[]> findDailyMetrics(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
