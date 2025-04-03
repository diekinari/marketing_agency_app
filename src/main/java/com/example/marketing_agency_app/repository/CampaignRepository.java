package com.example.marketing_agency_app.repository;

import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Метод для получения агрегированных метрик из представления
    @Query(value = "SELECT c.* FROM campaign_metrics c", nativeQuery = true)
    List<CampaignMetrics> findCampaignMetrics();
}
