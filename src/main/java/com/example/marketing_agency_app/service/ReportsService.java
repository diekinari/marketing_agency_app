package com.example.marketing_agency_app.service;

import com.example.marketing_agency_app.repository.CampaignMetricsRepository;
import com.example.marketing_agency_app.repository.DailyMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ReportsService {

    private final CampaignMetricsRepository campaignMetricsRepository;
    private final DailyMetricsRepository dailyMetricsRepository;

    public ReportsService(CampaignMetricsRepository campaignMetricsRepository, DailyMetricsRepository dailyMetricsRepository) {
        this.campaignMetricsRepository = campaignMetricsRepository;
        this.dailyMetricsRepository = dailyMetricsRepository;
    }

    public Map<String, Object> getDailyMetrics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = dailyMetricsRepository.findDailyMetrics(startDate, endDate);
        List<String> labels = new ArrayList<>();
        List<Long> impressions = new ArrayList<>();
        List<Long> clicks = new ArrayList<>();
        List<Long> conversions = new ArrayList<>();
        List<Double> spent = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Object[] row : rawData) {
            LocalDate day = ((java.sql.Date) row[0]).toLocalDate();
            Number imp = (Number) row[1];
            Number clk = (Number) row[2];
            Number conv = (Number) row[3];
            Number sp = (Number) row[4];
            labels.add(day.format(dtf));
            impressions.add(imp.longValue());
            clicks.add(clk.longValue());
            conversions.add(conv.longValue());
            spent.add(sp.doubleValue());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("impressions", impressions);
        result.put("clicks", clicks);
        result.put("conversions", conversions);
        result.put("spent", spent);
        return result;
    }

    // Базовая агрегация, например, динамика показов
    public Map<String, Object> getImpressionsChartData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = campaignMetricsRepository.findImpressionsGroupedByDate(startDate, endDate);
        List<String> labels = new ArrayList<>();
        List<Long> dataPoints = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Object[] row : rawData) {
            LocalDate date = (LocalDate) row[0];
            Long impressions = (Long) row[1];
            labels.add(date.format(formatter));
            dataPoints.add(impressions);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", dataPoints);
        return result;
    }

    // Расширенная агрегация для админов, например, динамика кликов или конверсий
    public Map<String, Object> getExtendedChartData(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = campaignMetricsRepository.findClicksGroupedByDate(startDate, endDate);
        List<String> labels = new ArrayList<>();
        List<Long> dataPoints = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Object[] row : rawData) {
            LocalDate date = (LocalDate) row[0];
            // Если сумма кликов возвращается как число, приведение к Long
            Long clicks = (row[1] != null) ? ((Number) row[1]).longValue() : 0L;
            labels.add(date.format(formatter));
            dataPoints.add(clicks);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", dataPoints);
        return result;
    }
}