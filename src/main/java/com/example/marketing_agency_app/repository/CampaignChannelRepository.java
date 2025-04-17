package com.example.marketing_agency_app.repository;


import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampaignChannelRepository extends JpaRepository<CampaignChannel, Long> {

    // Удаляет все записи CampaignChannel для заданной кампании,
    // у которых id канала (channel.channelId) содержится в списке channelIds.
    void deleteByCampaignAndChannelChannelIdIn(Campaign savedCampaign, List<Long> idsToDelete);

    void deleteByCampaign(Campaign savedCampaign);


    @Query("""
        SELECT 
          COALESCE(SUM(cc.impressions),0),
          COALESCE(SUM(cc.clicks),0),
          COALESCE(SUM(cc.conversions),0),
          COALESCE(SUM(cc.spentAmount),0)
        FROM CampaignChannel cc
        WHERE (:campaignId IS NULL OR cc.campaign.campaignId = :campaignId)
          AND cc.campaign.startDate <= :endDate
          AND cc.campaign.endDate   >= :startDate
    """)
    List<Object[]> findTotalsByCampaignPeriod(
            @Param("campaignId") Long campaignId,
            @Param("startDate")   LocalDate startDate,
            @Param("endDate")     LocalDate endDate
    );
}

