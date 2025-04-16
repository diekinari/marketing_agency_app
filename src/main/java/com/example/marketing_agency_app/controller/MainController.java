package com.example.marketing_agency_app.controller;

import com.example.marketing_agency_app.model.AudienceSegment;
import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignMetrics;
import com.example.marketing_agency_app.model.Channel;
import com.example.marketing_agency_app.service.AudienceService;
import com.example.marketing_agency_app.service.CampaignService;
import com.example.marketing_agency_app.service.ChannelService;
import com.example.marketing_agency_app.service.ReportsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {


    private final CampaignService campaignService;

    private final ChannelService channelService;

    private final AudienceService audienceService;

    private final ReportsService reportsService;

    public MainController(AudienceService audienceService, CampaignService campaignService, ChannelService channelService, ReportsService reportsService) {
        this.audienceService = audienceService;
        this.campaignService = campaignService;
        this.channelService = channelService;
        this.reportsService = reportsService;
    }


    // ------------------ Кампании ------------------

    // Главная страница (Dashboard) с динамическими данными кампаний
//    @GetMapping({"/", "/dashboard"})
//    public String dashboard(Model model, HttpServletRequest request) {
//        List<CampaignMetrics> campaignsMetrics = campaignService.findCampaignMetrics();
//        model.addAttribute("campaigns", campaignsMetrics);
//        model.addAttribute("requestURI", request.getRequestURI());
//        return "dashboard";
//    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model,
                            HttpServletRequest request,
                            @RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "startDate", required = false) String startDate,
                            @RequestParam(value = "endDate", required = false) String endDate,
                            @RequestParam(value = "status", required = false) String status) {

        // Например, можно использовать метод в сервисном слое, который фильтрует кампании по заданным параметрам.
        // Если такой метод отсутствует, можно реализовать фильтрацию в сервисе (или SQL-запрос).
        List<CampaignMetrics> campaignsMetrics = campaignService.findFilteredCampaignMetrics(name, startDate, endDate, status);

        model.addAttribute("campaigns", campaignsMetrics);
        model.addAttribute("requestURI", request.getRequestURI());
        return "dashboard";
    }


    // Страница со списком кампаний
    @GetMapping("/campaigns")
    public String campaigns(Model model, HttpServletRequest request) {
        List<Campaign> campaigns = campaignService.findAll();
        model.addAttribute("campaigns", campaigns);
        model.addAttribute("requestURI", request.getRequestURI());
        return "campaigns";
    }

    // Форма создания новой кампании (ADMIN)
    @GetMapping("/campaign/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newCampaign(Model model) {
        model.addAttribute("campaign", new Campaign());
        model.addAttribute("channels", channelService.findAll());
        model.addAttribute("audiences", audienceService.findAll());
        model.addAttribute("availableChannels", channelService.findAll());
        return "campaign_form";
    }

    // Сохранение кампании (ADMIN)
    @PostMapping("/campaign/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCampaign(@ModelAttribute("campaign") Campaign campaign,
                               @RequestParam(name = "channelIds", required = false) List<Long> channelIds,
                               @RequestParam(name = "channelBudgets", required = false) List<Double> channelBudgets,
                               @RequestParam(name = "newChannelIds", required = false) List<Long> newChannelIds,
                               @RequestParam(name = "newChannelBudgets", required = false) List<Double> newChannelBudgets,
                               @RequestParam(name = "deletedChannelIds", required = false) String deletedChannelIds,
                               @RequestParam(name = "audienceIds", required = false) List<Long> audienceIds) {

        campaignService.saveCampaignWithRelations(campaign,
                channelIds,
                channelBudgets,
                newChannelIds,
                newChannelBudgets,
                deletedChannelIds,
                audienceIds);
        return "redirect:/campaigns";
    }


    // Форма редактирования кампании (ADMIN)
    @GetMapping("/campaign/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCampaign(@PathVariable("id") Long id, Model model) {
        Campaign campaign = campaignService.findById(id);
        campaign.setAudienceIds(
                campaign.getCampaignAudiences().stream()
                        .map(ca -> ca.getAudience().getAudienceId())
                        .collect(Collectors.toList())
        );
        model.addAttribute("campaign", campaign);
        model.addAttribute("channels", channelService.findAll());
        model.addAttribute("audiences", audienceService.findAll());
        model.addAttribute("availableChannels", channelService.findAvailableChannels(campaign.getCampaignChannels()));
        return "campaign_form";
    }

    // Удаление кампании (ADMIN)
    @GetMapping("/campaign/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCampaign(@PathVariable("id") Long id) {
        campaignService.delete(id);
        return "redirect:/campaigns";
    }

    // ------------------ Каналы ------------------

    // Страница со списком каналов
    @GetMapping("/channels")
    public String channels(Model model, HttpServletRequest request) {
        List<Channel> channels = channelService.findAll();
        model.addAttribute("channels", channels);
        model.addAttribute("requestURI", request.getRequestURI());
        return "channels_management";
    }

    // Форма создания нового канала (ADMIN)
    @GetMapping("/channel/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newChannel(Model model) {
        model.addAttribute("channel", new Channel());
        return "channel_form";  // Создайте шаблон channel_form.html
    }

    // Сохранение канала (ADMIN)
    @PostMapping("/channel/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveChannel(@ModelAttribute("channel") Channel channel) {
        channelService.save(channel);
        return "redirect:/channels";
    }

    // Форма редактирования канала (ADMIN)
    @GetMapping("/channel/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editChannel(@PathVariable("id") Long id, Model model) {
        Channel channel = channelService.findById(id);
        if (channel == null) {
            return "redirect:/channels";
        }
        model.addAttribute("channel", channel);
        return "channel_form";
    }

    // Удаление канала (ADMIN)
    @GetMapping("/channel/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteChannel(@PathVariable("id") Long id) {
        channelService.delete(id);
        return "redirect:/channels";
    }

    // ------------------ Аудитории ------------------

    // Страница со списком сегментов аудитории
    @GetMapping("/audiences")
    public String audiences(Model model, HttpServletRequest request) {
        List<AudienceSegment> audiences = audienceService.findAll();
        model.addAttribute("audiences", audiences);
        model.addAttribute("requestURI", request.getRequestURI());
        return "audience_segments_management";
    }

    // Форма создания нового сегмента аудитории (ADMIN)
    @GetMapping("/audience/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newAudience(Model model) {
        model.addAttribute("audience", new AudienceSegment());
        return "audience_form";  // Создайте шаблон audience_form.html
    }

    // Сохранение сегмента аудитории (ADMIN)
    @PostMapping("/audience/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveAudience(@ModelAttribute("audience") AudienceSegment audience) {
        audienceService.save(audience);
        return "redirect:/audiences";
    }

    // Форма редактирования сегмента аудитории (ADMIN)
    @GetMapping("/audience/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editAudience(@PathVariable("id") Long id, Model model) {
        AudienceSegment audience = audienceService.findById(id);
        if (audience == null) {
            return "redirect:/audiences";
        }
        model.addAttribute("audience", audience);
        return "audience_form";
    }

    // Удаление сегмента аудитории (ADMIN)
    @GetMapping("/audience/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAudience(@PathVariable("id") Long id) {
        audienceService.delete(id);
        return "redirect:/audiences";
    }

    // ------------------ Отчёты и информация ------------------

    // Страница аналитики и отчетов
//    @GetMapping("/reports")
//    public String reports(Model model,
//                          @RequestParam(value = "startDate", required = false) String startDateStr,
//                          @RequestParam(value = "endDate", required = false) String endDateStr,
//                          Authentication authentication) {
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate startDate = (startDateStr != null && !startDateStr.isEmpty())
//                ? LocalDate.parse(startDateStr, dtf)
//                : LocalDate.now().minusMonths(1);
//        LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty())
//                ? LocalDate.parse(endDateStr, dtf)
//                : LocalDate.now();
//
//        // Получаем базовые данные для графика динамики показов
//        Map<String, Object> basicChartData = reportsService.getImpressionsChartData(startDate, endDate);
//        model.addAttribute("chartLabels", basicChartData.get("labels"));
//        model.addAttribute("chartData", basicChartData.get("data"));
//
//        // Добавляем сами значения дат для сохранения состояния формы
//        model.addAttribute("startDate", startDateStr);
//        model.addAttribute("endDate", endDateStr);
//
//        // Если пользователь имеет роль ADMIN, добавляем расширенные данные.
//        if (authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
//            Map<String, Object> extendedData = reportsService.getExtendedChartData(startDate, endDate);
//            model.addAttribute("extendedChartLabels", extendedData.get("labels"));
//            model.addAttribute("extendedChartData", extendedData.get("data"));
//            // Также можно добавить таблицу детальных показателей, статистику по каналам и т.д.
//        }
//
//        return "analytics_reports";  // шаблон с аналитикой
//    }

    @GetMapping("/reports")
    public String reports(Model model,
                          @RequestParam(value = "startDate", required = false) String startDateStr,
                          @RequestParam(value = "endDate", required = false) String endDateStr) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = (startDateStr != null && !startDateStr.isEmpty())
                ? LocalDate.parse(startDateStr, dtf)
                : LocalDate.now().minusMonths(1);
        LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty())
                ? LocalDate.parse(endDateStr, dtf)
                : LocalDate.now();

        Map<String, Object> dailyMetrics = reportsService.getDailyMetrics(startDate, endDate);

        model.addAttribute("chartLabels", dailyMetrics.get("labels"));
        model.addAttribute("impressionsData", dailyMetrics.get("impressions"));
        model.addAttribute("clicksData", dailyMetrics.get("clicks"));
        model.addAttribute("conversionsData", dailyMetrics.get("conversions"));
        model.addAttribute("spentData", dailyMetrics.get("spent"));

        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);

        return "analytics_reports";
    }

    // Страница "Об авторе"
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
