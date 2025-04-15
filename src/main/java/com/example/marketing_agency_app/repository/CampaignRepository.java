package com.example.marketing_agency_app.repository;

import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Метод для получения агрегированных метрик из представления
    @Query(value = "SELECT c.* FROM campaign_metrics c", nativeQuery = true)
    List<CampaignMetrics> findCampaignMetrics();

    // Метод фильтрации кампаний.
    // Используем оператор '||' для конкатенации строк в PostgreSQL и добавляем проверку на пустую строку.
    @Query("SELECT c FROM CampaignMetrics c " +
            "WHERE (COALESCE(:name, '') = '' OR LOWER(c.name) LIKE CONCAT('%', LOWER(:name), '%')) " +
            "  AND (COALESCE(:status, '') = '' OR c.status = :status) " +
            "  AND (c.startDate = COALESCE(:startDate, c.startDate)) " +
            "  AND (c.endDate = COALESCE(:endDate, c.endDate))")
    List<CampaignMetrics> findFilteredCampaignMetrics(@Param("name") String name,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate,
                                                      @Param("status") String status);


    @Query("SELECT c FROM CampaignMetrics c " +
            "WHERE LOWER(c.name) LIKE CONCAT('%', LOWER(:name), '%')")
    List<CampaignMetrics> findByNameTest(@Param("name") String name);

    // Метод для полного получения кампаний со связанными данными:
    @Query("SELECT DISTINCT c FROM Campaign c " +
            "LEFT JOIN FETCH c.campaignChannels cc " +
            "LEFT JOIN FETCH c.campaignAudiences ca")
    List<Campaign> findAllWithAssociations();

}
