package com.example.marketing_agency_app.service;


import com.example.marketing_agency_app.model.AudienceSegment;
import com.example.marketing_agency_app.repository.AudienceSegmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AudienceService {

    private final AudienceSegmentRepository audienceSegmentRepository;

    public AudienceService(AudienceSegmentRepository audienceSegmentRepository) {
        this.audienceSegmentRepository = audienceSegmentRepository;
    }

    public List<AudienceSegment> findAll() {
        return audienceSegmentRepository.findAll();
    }

    public AudienceSegment findById(Long id) {
        return audienceSegmentRepository.findById(id).orElse(null);
    }

    public AudienceSegment save(AudienceSegment audienceSegment) {
        return audienceSegmentRepository.save(audienceSegment);
    }

    public void delete(Long id) {
        audienceSegmentRepository.deleteById(id);
    }
}
