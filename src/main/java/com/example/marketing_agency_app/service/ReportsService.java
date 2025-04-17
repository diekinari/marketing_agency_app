package com.example.marketing_agency_app.service;

import com.example.marketing_agency_app.repository.CampaignChannelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReportsService {

    private final CampaignChannelRepository channelRepo;
    private final Random rnd = new Random();


    public ReportsService(CampaignChannelRepository channelRepo) {
        this.channelRepo = channelRepo;
    }


    /**
     * Возвращает «реалистичное» распределение метрик по дням
     * для конкретной кампании (campaignId) или по всем (если campaignId == null).
     */
    public Map<String, Object> getDailyMetrics(Long campaignId, LocalDate startDate, LocalDate endDate) {
        // 1) Получаем список строк (в нашем случае — ровно одну строку)
        List<Object[]> rows = channelRepo.findTotalsByCampaignPeriod(campaignId, startDate, endDate);

        // 2) Если список пуст (нет данных) — берём нули
        Object[] totals = rows.isEmpty()
                ? new Object[]{0L, 0L, 0L, 0.0}
                : rows.get(0);

        // 3) Безопасно приводим каждый элемент к Number
        long totalImpr = ((Number) totals[0]).longValue();
        long totalClicks = ((Number) totals[1]).longValue();
        long totalConv = ((Number) totals[2]).longValue();
        double totalSpent = ((Number) totals[3]).doubleValue();

        // 4) Остальная логика генерации распределения…
        int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> labels = new ArrayList<>(days);
        List<Long> imprSeries = randomDistrib(totalImpr, days);
        List<Long> clickSeries = randomDistrib(totalClicks, days);
        List<Long> convSeries = randomDistrib(totalConv, days);
        List<Double> spentSeries = randomDistribDouble(totalSpent, days);

        for (int i = 0; i < days; i++) {
            labels.add(startDate.plusDays(i).format(dtf));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("impressions", imprSeries);
        result.put("clicks", clickSeries);
        result.put("conversions", convSeries);
        result.put("spent", spentSeries);
        return result;
    }

    // Интеллектуальное случайное распределение целых
    private List<Long> randomDistrib(long total, int parts) {
        double[] w = new double[parts];
        double sum = 0;
        for (int i = 0; i < parts; i++) {
            w[i] = rnd.nextDouble();
            sum += w[i];
        }
        List<Long> out = new ArrayList<>(parts);
        long acc = 0;
        for (int i = 0; i < parts; i++) {
            long v = (i < parts - 1)
                    ? Math.round(w[i] / sum * total)
                    : total - acc;
            out.add(v);
            acc += v;
        }
        return out;
    }

    // И для дробных (spent)
    private List<Double> randomDistribDouble(double total, int parts) {
        double[] w = new double[parts];
        double sum = 0;
        for (int i = 0; i < parts; i++) {
            w[i] = rnd.nextDouble();
            sum += w[i];
        }
        List<Double> out = new ArrayList<>(parts);
        double acc = 0;
        for (int i = 0; i < parts; i++) {
            double v = (i < parts - 1)
                    ? (w[i] / sum * total)
                    : (total - acc);
            v = Math.round(v * 100.0) / 100.0;
            out.add(v);
            acc += v;
        }
        return out;
    }

}