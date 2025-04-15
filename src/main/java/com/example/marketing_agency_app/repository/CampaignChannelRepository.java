package com.example.marketing_agency_app.repository;


import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignChannelRepository extends JpaRepository<CampaignChannel, Long> {

    // Удаляет все записи CampaignChannel для заданной кампании,
    // у которых id канала (channel.channelId) содержится в списке channelIds.
    void deleteByCampaignAndChannelChannelIdIn(Campaign savedCampaign, List<Long> idsToDelete);

    void deleteByCampaign(Campaign savedCampaign);
}

