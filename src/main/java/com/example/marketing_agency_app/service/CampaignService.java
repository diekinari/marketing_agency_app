package com.example.marketing_agency_app.service;


import com.example.marketing_agency_app.model.*;
import com.example.marketing_agency_app.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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


    public void saveCampaignWithRelations(
            Campaign campaign,
            List<Long> channelIds,
            List<Double> channelBudgets,
            List<Integer> channelImpressions,
            List<Integer> channelClicks,
            List<Integer> channelConversions,
            List<BigDecimal> channelSpentAmounts,
            List<Long> newChannelIds,
            List<Double> newChannelBudgets,
            List<Integer> newChannelImpressions,
            List<Integer> newChannelClicks,
            List<Integer> newChannelConversions,
            List<BigDecimal> newChannelSpentAmounts,
            String deletedChannelIds,
            List<Long> audienceIds
    ) {
        Campaign savedCampaign = campaignRepository.save(campaign);

        // 2. Удаление
        if (deletedChannelIds != null && !deletedChannelIds.trim().isEmpty()) {
            List<Long> idsToDelete = Arrays.stream(deletedChannelIds.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).map(Long::valueOf)
                    .collect(Collectors.toList());
            campaignChannelRepository.deleteByCampaignAndChannelChannelIdIn(savedCampaign, idsToDelete);
        }

        // 3. Обновление существующих каналов
        if (channelIds != null && channelBudgets != null && channelIds.size() == channelBudgets.size()) {
            for (int i = 0; i < channelIds.size(); i++) {
                Long channelId = channelIds.get(i);
                BigDecimal allocated = BigDecimal.valueOf(
                        channelBudgets.get(i) == null ? 0.0 : channelBudgets.get(i)
                );
                CampaignChannel existing = savedCampaign.getCampaignChannels().stream()
                        .filter(cc -> cc.getChannel().getChannelId().equals(channelId))
                        .findFirst().orElse(null);
                if (existing != null) {
                    existing.setAllocatedBudget(allocated);
                    existing.setImpressions(
                            (channelImpressions != null && i < channelImpressions.size() && channelImpressions.get(i) != null)
                                    ? channelImpressions.get(i) : 0
                    );
                    existing.setClicks(
                            (channelClicks != null && i < channelClicks.size() && channelClicks.get(i) != null)
                                    ? channelClicks.get(i) : 0
                    );
                    existing.setConversions(
                            (channelConversions != null && i < channelConversions.size() && channelConversions.get(i) != null)
                                    ? channelConversions.get(i) : 0
                    );
                    existing.setSpentAmount(
                            (channelSpentAmounts != null && i < channelSpentAmounts.size() && channelSpentAmounts.get(i) != null)
                                    ? channelSpentAmounts.get(i) : BigDecimal.ZERO
                    );
                    campaignChannelRepository.save(existing);
                } else {
                    // Если связи нет — создаём новую так же, как в блоке добавления
                    Channel channel = channelRepository.findById(channelId).orElse(null);
                    if (channel != null) {
                        CampaignChannel cc = new CampaignChannel();
                        cc.setCampaign(savedCampaign);
                        cc.setChannel(channel);
                        cc.setAllocatedBudget(allocated);
                        cc.setImpressions(
                                (channelImpressions != null && i < channelImpressions.size() && channelImpressions.get(i) != null)
                                        ? channelImpressions.get(i) : 0
                        );
                        cc.setClicks(
                                (channelClicks != null && i < channelClicks.size() && channelClicks.get(i) != null)
                                        ? channelClicks.get(i) : 0
                        );
                        cc.setConversions(
                                (channelConversions != null && i < channelConversions.size() && channelConversions.get(i) != null)
                                        ? channelConversions.get(i) : 0
                        );
                        cc.setSpentAmount(
                                (channelSpentAmounts != null && i < channelSpentAmounts.size() && channelSpentAmounts.get(i) != null)
                                        ? channelSpentAmounts.get(i) : BigDecimal.ZERO
                        );
                        campaignChannelRepository.save(cc);
                    }
                }
            }
        }

        // 4. Добавление новых каналов — без изменений
        if (newChannelIds != null && newChannelBudgets != null && newChannelIds.size() == newChannelBudgets.size()) {
            for (int i = 0; i < newChannelIds.size(); i++) {
                Long channelId = newChannelIds.get(i);
                if (channelId == null) continue;
                BigDecimal allocated = BigDecimal.valueOf(
                        newChannelBudgets.get(i) == null ? 0.0 : newChannelBudgets.get(i)
                );
                boolean exists = savedCampaign.getCampaignChannels().stream()
                        .anyMatch(cc -> cc.getChannel().getChannelId().equals(channelId));
                if (!exists) {
                    Channel channel = channelRepository.findById(channelId).orElse(null);
                    if (channel != null) {
                        CampaignChannel cc = new CampaignChannel();
                        cc.setCampaign(savedCampaign);
                        cc.setChannel(channel);
                        cc.setAllocatedBudget(allocated);
                        cc.setImpressions(
                                (newChannelImpressions != null && i < newChannelImpressions.size() && newChannelImpressions.get(i) != null)
                                        ? newChannelImpressions.get(i) : 0
                        );
                        cc.setClicks(
                                (newChannelClicks != null && i < newChannelClicks.size() && newChannelClicks.get(i) != null)
                                        ? newChannelClicks.get(i) : 0
                        );
                        cc.setConversions(
                                (newChannelConversions != null && i < newChannelConversions.size() && newChannelConversions.get(i) != null)
                                        ? newChannelConversions.get(i) : 0
                        );
                        cc.setSpentAmount(
                                (newChannelSpentAmounts != null && i < newChannelSpentAmounts.size() && newChannelSpentAmounts.get(i) != null)
                                        ? newChannelSpentAmounts.get(i) : BigDecimal.ZERO
                        );
                        campaignChannelRepository.save(cc);
                    }
                }
            }
        }

        // 5. Сегменты аудитории — без изменений
        campaignAudienceRepository.deleteByCampaign(savedCampaign);
        if (audienceIds != null) {
            for (Long aid : audienceIds) {
                AudienceSegment aud = audienceSegmentRepository.findById(aid).orElse(null);
                if (aud != null) {
                    CampaignAudience ca = new CampaignAudience();
                    ca.setCampaign(savedCampaign);
                    ca.setAudience(aud);
                    campaignAudienceRepository.save(ca);
                }
            }
        }
    }


    public void delete(Long id) {
        campaignRepository.deleteById(id);
    }

    public List<CampaignMetrics> findFilteredCampaignMetrics(String name, String startDateStr, String endDateStr, String status, Sort sort) {
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

        return campaignRepository.findFilteredCampaignMetrics(name, startDate, endDate, status, sort);
    }

}
