package com.example.marketing_agency_app.repository;


import com.example.marketing_agency_app.model.CampaignChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignChannelRepository extends JpaRepository<CampaignChannel, Long> {
}

