package com.example.marketing_agency_app.repository;


import com.example.marketing_agency_app.model.AudienceSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienceSegmentRepository extends JpaRepository<AudienceSegment, Long> {
}

