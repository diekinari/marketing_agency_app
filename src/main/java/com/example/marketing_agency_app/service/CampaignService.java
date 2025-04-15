package com.example.marketing_agency_app.service;


import com.example.marketing_agency_app.model.*;
import com.example.marketing_agency_app.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return campaignRepository.findAllWithAssociations();
    }


    public Campaign findById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }


    public void saveCampaignWithRelations(Campaign campaign,
                                          List<Long> channelIds,            // id для уже существующих каналов, оставшихся в форме
                                          List<Double> channelBudgets,       // их бюджеты
                                          List<Long> newChannelIds,          // id новых каналов, добавленных через форму
                                          List<Double> newChannelBudgets,     // их бюджеты
                                          String deletedChannelIds,          // строка, содержащая id удалённых каналов (разделённые запятыми)
                                          List<Long> audienceIds) {
        // 1. Сохраняем основную кампанию.
        // Если кампания редактируется, она имеет ненулевой id — Hibernate выполнит merge (update).
        Campaign savedCampaign = campaignRepository.save(campaign);

        // 2. Удаляем те связи, которые пользователь явно удалил.
        if (deletedChannelIds != null && !deletedChannelIds.trim().isEmpty()) {
            List<Long> idsToDelete = Arrays.stream(deletedChannelIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            campaignChannelRepository.deleteByCampaignAndChannelChannelIdIn(savedCampaign, idsToDelete);
        }

        // 3. Обработка обновления существующих каналов.
        // Здесь мы не удаляем все связи, а лишь обновляем те, которые остались в форме.
        if (channelIds != null && channelBudgets != null && channelIds.size() == channelBudgets.size()) {
            for (int i = 0; i < channelIds.size(); i++) {
                Long channelId = channelIds.get(i);
                Double budget = channelBudgets.get(i);
                BigDecimal allocatedBudget = (budget == null) ? BigDecimal.ZERO : BigDecimal.valueOf(budget);

                // Ищем связь для этого канала среди уже существующих связей кампании
                CampaignChannel existing = savedCampaign.getCampaignChannels().stream()
                        .filter(cc -> cc.getChannel().getChannelId().equals(channelId))
                        .findFirst()
                        .orElse(null);
                if (existing != null) {
                    // Обновляем бюджет, если связь существует (остальные метрики оставляем прежними)
                    existing.setAllocatedBudget(allocatedBudget);
                    campaignChannelRepository.save(existing);
                } else {
                    // Если такой связи нет (в редактируемой форме обычно должна быть, но на всякий случай) – создаём новую.
                    Channel channel = channelRepository.findById(channelId).orElse(null);
                    if (channel != null) {
                        CampaignChannel cc = new CampaignChannel();
                        cc.setCampaign(savedCampaign);
                        cc.setChannel(channel);
                        cc.setAllocatedBudget(allocatedBudget);
                        cc.setImpressions(0);
                        cc.setClicks(0);
                        cc.setConversions(0);
                        cc.setSpentAmount(BigDecimal.ZERO);
                        campaignChannelRepository.save(cc);
                    }
                }
            }
        }

        // 4. Обработка добавления новых каналов.
        if (newChannelIds != null && newChannelBudgets != null && newChannelIds.size() == newChannelBudgets.size()) {
            for (int i = 0; i < newChannelIds.size(); i++) {
                Long channelId = newChannelIds.get(i);
                if (channelId == null) {
                    continue; // если пользователь не выбрал канал в новом блоке, пропускаем.
                }
                Double budget = newChannelBudgets.get(i);
                BigDecimal allocatedBudget = (budget == null) ? BigDecimal.ZERO : BigDecimal.valueOf(budget);

                // Проверяем, не была ли уже добавлена связь с этим каналом (на всякий случай)
                boolean exists = savedCampaign.getCampaignChannels().stream()
                        .anyMatch(cc -> cc.getChannel().getChannelId().equals(channelId));
                if (!exists) {
                    Channel channel = channelRepository.findById(channelId).orElse(null);
                    if (channel != null) {
                        CampaignChannel cc = new CampaignChannel();
                        cc.setCampaign(savedCampaign);
                        cc.setChannel(channel);
                        cc.setAllocatedBudget(allocatedBudget);
                        cc.setImpressions(0);
                        cc.setClicks(0);
                        cc.setConversions(0);
                        cc.setSpentAmount(BigDecimal.ZERO);
                        campaignChannelRepository.save(cc);
                    }
                }
            }
        }

        // 5. Обработка сегментов аудитории:
        // Для удобства удаляем все существующие связи и вставляем новые. Если требуется сохранить старые — можно изменить логику.
        campaignAudienceRepository.deleteByCampaign(savedCampaign);
        if (audienceIds != null) {
            for (Long audienceId : audienceIds) {
                if (audienceId == null) continue;
                AudienceSegment audience = audienceSegmentRepository.findById(audienceId).orElse(null);
                if (audience != null) {
                    CampaignAudience ca = new CampaignAudience();
                    ca.setCampaign(savedCampaign);
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
