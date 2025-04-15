package com.example.marketing_agency_app.repository;

import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignAudience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignAudienceRepository extends JpaRepository<CampaignAudience, Long> {
    void deleteByCampaign(Campaign savedCampaign);
}

