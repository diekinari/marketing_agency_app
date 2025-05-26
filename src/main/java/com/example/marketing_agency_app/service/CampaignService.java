package com.example.marketing_agency_app.service;

import com.example.marketing_agency_app.model.*;
import com.example.marketing_agency_app.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления сущностью Campaign и связанных с ней объектов:
 * CampaignChannel и CampaignAudience.
 */
@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ChannelRepository channelRepository;
    private final AudienceSegmentRepository audienceSegmentRepository;
    private final CampaignChannelRepository campaignChannelRepository;
    private final CampaignAudienceRepository campaignAudienceRepository;

    /**
     * Конструктор сервиса, внедряющий необходимые репозитории.
     *
     * @param campaignRepository           репозиторий кампаний
     * @param channelRepository            репозиторий каналов
     * @param audienceSegmentRepository    репозиторий сегментов аудитории
     * @param campaignChannelRepository    репозиторий связей кампания–канал
     * @param campaignAudienceRepository   репозиторий связей кампания–аудитория
     */
    public CampaignService(CampaignRepository campaignRepository,
                           ChannelRepository channelRepository,
                           AudienceSegmentRepository audienceSegmentRepository,
                           CampaignChannelRepository campaignChannelRepository,
                           CampaignAudienceRepository campaignAudienceRepository) {
        this.campaignRepository = campaignRepository;
        this.channelRepository = channelRepository;
        this.audienceSegmentRepository = audienceSegmentRepository;
        this.campaignChannelRepository = campaignChannelRepository;
        this.campaignAudienceRepository = campaignAudienceRepository;
    }

    /**
     * Возвращает агрегированные метрики всех кампаний.
     *
     * @return список DTO CampaignMetrics
     */
    public List<CampaignMetrics> findCampaignMetrics() {
        return campaignRepository.findCampaignMetrics();
    }

    /**
     * Возвращает все кампании вместе с их связанными каналами и аудиториями.
     *
     * @return список Campaign
     */
    public List<Campaign> findAll() {
        return campaignRepository.findAllWithAssociations();
    }

    /**
     * Находит кампанию по её идентификатору.
     *
     * @param id идентификатор кампании
     * @return объект Campaign или null, если не найден
     */
    public Campaign findById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }

    /**
     * Сохраняет или обновляет кампанию вместе с её связями:
     * существующими каналами, новыми каналами и сегментами аудитории.
     * Удалённые связи кампании–канала передаются строкой идентификаторов.
     *
     * @param campaign                объект Campaign для сохранения
     * @param channelIds              список id уже существующих каналов
     * @param channelBudgets          бюджеты для существующих каналов
     * @param channelImpressions      показы для существующих каналов
     * @param channelClicks           клики для существующих каналов
     * @param channelConversions      конверсии для существующих каналов
     * @param channelSpentAmounts     потраченные суммы для существующих каналов
     * @param newChannelIds           список id новых каналов
     * @param newChannelBudgets       бюджеты для новых каналов
     * @param newChannelImpressions   показы для новых каналов
     * @param newChannelClicks        клики для новых каналов
     * @param newChannelConversions   конверсии для новых каналов
     * @param newChannelSpentAmounts  потраченные суммы для новых каналов
     * @param deletedChannelIds       CSV строка id каналов, удалённых из кампании
     * @param audienceIds             список id сегментов аудитории
     */
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
        // Сохраняем или обновляем кампанию
        Campaign savedCampaign = campaignRepository.save(campaign);

        // Удаляем помеченные связи кампания–канал
        if (deletedChannelIds != null && !deletedChannelIds.trim().isEmpty()) {
            List<Long> idsToDelete = Arrays.stream(deletedChannelIds.split(","))
                    .map(String::trim).filter(s -> !s.isEmpty()).map(Long::valueOf)
                    .collect(Collectors.toList());
            campaignChannelRepository.deleteByCampaignAndChannelChannelIdIn(savedCampaign, idsToDelete);
        }

        // Обновляем существующие каналы
        if (channelIds != null && channelBudgets != null && channelIds.size() == channelBudgets.size()) {
            for (int i = 0; i < channelIds.size(); i++) {
                Long chanId = channelIds.get(i);
                BigDecimal allocated = BigDecimal.valueOf(
                        channelBudgets.get(i) == null ? 0.0 : channelBudgets.get(i)
                );
                CampaignChannel existing = savedCampaign.getCampaignChannels().stream()
                        .filter(cc -> cc.getChannel().getChannelId().equals(chanId))
                        .findFirst().orElse(null);
                if (existing != null) {
                    existing.setAllocatedBudget(allocated);
                    existing.setImpressions(
                            channelImpressions != null && i < channelImpressions.size() && channelImpressions.get(i) != null
                                    ? channelImpressions.get(i) : 0
                    );
                    existing.setClicks(
                            channelClicks != null && i < channelClicks.size() && channelClicks.get(i) != null
                                    ? channelClicks.get(i) : 0
                    );
                    existing.setConversions(
                            channelConversions != null && i < channelConversions.size() && channelConversions.get(i) != null
                                    ? channelConversions.get(i) : 0
                    );
                    existing.setSpentAmount(
                            channelSpentAmounts != null && i < channelSpentAmounts.size() && channelSpentAmounts.get(i) != null
                                    ? channelSpentAmounts.get(i) : BigDecimal.ZERO
                    );
                    campaignChannelRepository.save(existing);
                } else {
                    // Создаём новую связь, если её не было
                    Channel channel = channelRepository.findById(chanId).orElse(null);
                    if (channel != null) {
                        CampaignChannel cc = new CampaignChannel();
                        cc.setCampaign(savedCampaign);
                        cc.setChannel(channel);
                        cc.setAllocatedBudget(allocated);
                        cc.setImpressions(
                                channelImpressions != null && i < channelImpressions.size() && channelImpressions.get(i) != null
                                        ? channelImpressions.get(i) : 0
                        );
                        cc.setClicks(
                                channelClicks != null && i < channelClicks.size() && channelClicks.get(i) != null
                                        ? channelClicks.get(i) : 0
                        );
                        cc.setConversions(
                                channelConversions != null && i < channelConversions.size() && channelConversions.get(i) != null
                                        ? channelConversions.get(i) : 0
                        );
                        cc.setSpentAmount(
                                channelSpentAmounts != null && i < channelSpentAmounts.size() && channelSpentAmounts.get(i) != null
                                        ? channelSpentAmounts.get(i) : BigDecimal.ZERO
                        );
                        campaignChannelRepository.save(cc);
                    }
                }
            }
        }

        // Добавляем новые каналы
        if (newChannelIds != null && newChannelBudgets != null && newChannelIds.size() == newChannelBudgets.size()) {
            for (int i = 0; i < newChannelIds.size(); i++) {
                Long chanId = newChannelIds.get(i);
                if (chanId == null) continue;
                BigDecimal allocated = BigDecimal.valueOf(
                        newChannelBudgets.get(i) == null ? 0.0 : newChannelBudgets.get(i)
                );
                boolean exists = savedCampaign.getCampaignChannels().stream()
                        .anyMatch(cc -> cc.getChannel().getChannelId().equals(chanId));
                if (!exists) {
                    Channel channel = channelRepository.findById(chanId).orElse(null);
                    if (channel != null) {
                        CampaignChannel cc = new CampaignChannel();
                        cc.setCampaign(savedCampaign);
                        cc.setChannel(channel);
                        cc.setAllocatedBudget(allocated);
                        cc.setImpressions(
                                newChannelImpressions != null && i < newChannelImpressions.size() && newChannelImpressions.get(i) != null
                                        ? newChannelImpressions.get(i) : 0
                        );
                        cc.setClicks(
                                newChannelClicks != null && i < newChannelClicks.size() && newChannelClicks.get(i) != null
                                        ? newChannelClicks.get(i) : 0
                        );
                        cc.setConversions(
                                newChannelConversions != null && i < newChannelConversions.size() && newChannelConversions.get(i) != null
                                        ? newChannelConversions.get(i) : 0
                        );
                        cc.setSpentAmount(
                                newChannelSpentAmounts != null && i < newChannelSpentAmounts.size() && newChannelSpentAmounts.get(i) != null
                                        ? newChannelSpentAmounts.get(i) : BigDecimal.ZERO
                        );
                        campaignChannelRepository.save(cc);
                    }
                }
            }
        }

        // Пересохраняем связи с аудиториями
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

    /**
     * Удаляет кампанию по её идентификатору.
     *
     * @param id идентификатор кампании
     */
    public void delete(Long id) {
        campaignRepository.deleteById(id);
    }

    /**
     * Ищет кампании по заданным фильтрам и сортировке.
     *
     * @param name          часть названия кампании
     * @param startDateStr  строка с датой начала (yyyy-MM-dd)
     * @param endDateStr    строка с датой окончания (yyyy-MM-dd)
     * @param status        статус кампании
     * @param channelId     идентификатор канала
     * @param minBudget     минимальный бюджет
     * @param maxBudget     максимальный бюджет
     * @param minRoi        минимальный ROI
     * @param maxRoi        максимальный ROI
     * @param sort          объект Sort для сортировки
     * @return              список CampaignMetrics, удовлетворяющих условиям
     */
    public List<CampaignMetrics> findFilteredCampaignMetrics(
            String name,
            String startDateStr,
            String endDateStr,
            String status,
            Long channelId,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            BigDecimal minRoi,
            BigDecimal maxRoi,
            Sort sort
    ) {
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
            // здесь можно добавить логирование
            System.err.println("Ошибка парсинга даты: " + e.getMessage());
        }

        return campaignRepository.findFilteredCampaignMetrics(
                name, startDate, endDate, status,
                channelId, minBudget, maxBudget, minRoi, maxRoi,
                sort
        );
    }
}
