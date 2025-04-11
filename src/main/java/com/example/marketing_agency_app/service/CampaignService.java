package com.example.marketing_agency_app.service;


import com.example.marketing_agency_app.model.*;
import com.example.marketing_agency_app.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CampaignService {


    private final CampaignRepository campaignRepository;
    private final ChannelRepository channelRepository;
    private final AudienceSegmentRepository audienceSegmentRepository;
    private final CampaignChannelRepository campaignChannelRepository;
    private final CampaignAudienceRepository campaignAudienceRepository;

    public CampaignService(CampaignRepository campaignRepository, ChannelRepository channelRepository, AudienceSegmentRepository audienceSegmentRepository, CampaignChannelRepository campaignChannelRepository, CampaignAudienceRepository campaignAudienceRepository) {
        this.campaignRepository = campaignRepository;
        this.channelRepository = channelRepository;
        this.audienceSegmentRepository = audienceSegmentRepository;
        this.campaignChannelRepository = campaignChannelRepository;
        this.campaignAudienceRepository = campaignAudienceRepository;

    }

    // Метод для получения агрегированных метрик кампаний.
    // Предполагается, что CampaignRepository содержит кастомный запрос или использование представления
    public List<CampaignMetrics> findCampaignMetrics() {
        return campaignRepository.findCampaignMetrics();
    }


    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }


    public Campaign findById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }


    public void saveCampaignWithRelations(Campaign campaign, List<Long> channelIds, List<Double> channelBudgets, List<Long> audienceIds) {
        // Сначала сохраняем основную кампанию
        Campaign savedCampaign = campaignRepository.save(campaign);

        // Сохранение связи с каналами
        if (channelIds != null && channelBudgets != null && channelIds.size() == channelBudgets.size()) {
            for (int i = 0; i < channelIds.size(); i++) {
                Long channelId = channelIds.get(i);
                Double budget = channelBudgets.get(i);
                // Создаем связь Campaign_Channel
                CampaignChannel cc = new CampaignChannel();
                cc.setCampaign(savedCampaign);
                // Находим канал по ID
                Channel channel = channelRepository.findById(channelId).orElse(null);
                if (channel != null) {
                    cc.setChannel(channel);
                    cc.setAllocatedBudget(BigDecimal.valueOf(budget));
                    // Инициализируем метрики (показы, клики, конверсии, потраченная сумма)
                    cc.setImpressions(0);
                    cc.setClicks(0);
                    cc.setConversions(0);
                    cc.setSpentAmount(BigDecimal.valueOf(0.0));
                    // Дополнительно можно установить значения для cost_per_click, cost_per_conversion, CTR и т.п.
                    campaignChannelRepository.save(cc);
                }
            }
        }

        // Сохранение связи с сегментами аудитории
        if (audienceIds != null) {
            for (Long audienceId : audienceIds) {
                CampaignAudience ca = new CampaignAudience();
                ca.setCampaign(savedCampaign);
                AudienceSegment audience = audienceSegmentRepository.findById(audienceId).orElse(null);
                if (audience != null) {
                    ca.setAudience(audience);
                    campaignAudienceRepository.save(ca);
                }
            }
        }
    }


    public void delete(Long id) {
        campaignRepository.deleteById(id);
    }

    public List<CampaignMetrics> findFilteredCampaignMetrics(String name, String startDateStr, String endDateStr, String status) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        try {
            if (startDateStr != null && !startDateStr.isBlank()) {
                startDate = LocalDate.parse(startDateStr);
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                endDate = LocalDate.parse(endDateStr);
            }
        } catch (Exception e) {
            // Можно добавить логирование или обработку неверного формата даты
            System.err.println("Ошибка преобразования даты: " + e.getMessage());
        }
        System.out.println("Name: " + name);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Status: " + status);
        return campaignRepository.findFilteredCampaignMetrics(name, startDate, endDate, status);
    }
}
