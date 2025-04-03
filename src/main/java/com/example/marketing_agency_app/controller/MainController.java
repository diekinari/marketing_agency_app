package com.example.marketing_agency_app.controller;

import com.example.marketing_agency_app.model.AudienceSegment;
import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.Channel;
import com.example.marketing_agency_app.model.CampaignMetrics;
import com.example.marketing_agency_app.service.AudienceService;
import com.example.marketing_agency_app.service.CampaignService;
import com.example.marketing_agency_app.service.ChannelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private AudienceService audienceService;

    // Главная страница (Dashboard) с динамическими данными кампаний
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, HttpServletRequest request) {
        // Получаем агрегированные метрики для всех кампаний
        List<CampaignMetrics> campaignsMetrics = campaignService.findCampaignMetrics();
        model.addAttribute("campaigns", campaignsMetrics);
        model.addAttribute("requestURI", request.getRequestURI());
        return "dashboard";  // thymeleaf шаблон dashboard.html
    }

    // Страница со списком кампаний (доступна для всех пользователей)
    @GetMapping("/campaigns")
    public String campaigns(Model model, HttpServletRequest request) {
        List<Campaign> campaigns = campaignService.findAll();
        model.addAttribute("campaigns", campaigns);
        model.addAttribute("requestURI", request.getRequestURI());
        return "campaigns";  // thymeleaf шаблон campaigns.html
    }

    // Форма создания новой кампании (только для ADMIN)
    @GetMapping("/campaign/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newCampaign(Model model) {
        model.addAttribute("campaign", new Campaign());
        model.addAttribute("channels", channelService.findAll());
        model.addAttribute("audiences", audienceService.findAll());
        return "campaign_form";  // thymeleaf шаблон campaign_form.html
    }

    // Сохранение кампании (создание/редактирование) – доступно только ADMIN
    @PostMapping("/campaign/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCampaign(@ModelAttribute("campaign") Campaign campaign,
                               @RequestParam(name = "channelIds", required = false) List<Long> channelIds,
                               @RequestParam(name = "channelBudgets", required = false) List<Double> channelBudgets,
                               @RequestParam(name = "audienceIds", required = false) List<Long> audienceIds) {
        // Логика обработки выбранных каналов и аудиторий реализуется в сервисном слое
        campaignService.saveCampaignWithRelations(campaign, channelIds, channelBudgets, audienceIds);
        return "redirect:/campaigns";
    }

    // Форма редактирования кампании (только для ADMIN)
    @GetMapping("/campaign/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCampaign(@PathVariable("id") Long id, Model model) {
        Campaign campaign = campaignService.findById(id);
        model.addAttribute("campaign", campaign);
        model.addAttribute("channels", channelService.findAll());
        model.addAttribute("audiences", audienceService.findAll());
        return "campaign_form";  // используем тот же шаблон для создания/редактирования
    }

    // Удаление кампании (только для ADMIN)
    @GetMapping("/campaign/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCampaign(@PathVariable("id") Long id) {
        campaignService.delete(id);
        return "redirect:/campaigns";
    }

    // Страница управления каналами (открыта для всех пользователей, но функционал редактирования/удаления может быть ограничен в шаблоне)
    @GetMapping("/channels")
    public String channels(Model model) {
        List<Channel> channels = channelService.findAll();
        model.addAttribute("channels", channels);
        return "channels_management"; // thymeleaf шаблон channels_management.html
    }

    // Страница управления сегментами аудитории
    @GetMapping("/audiences")
    public String audiences(Model model) {
        List<AudienceSegment> audiences = audienceService.findAll();
        model.addAttribute("audiences", audiences);
        return "audience_segments_management"; // thymeleaf шаблон audience_segments_management.html
    }

    // Страница аналитики и отчетов (доступна для всех пользователей)
    @GetMapping("/reports")
    public String reports(Model model) {
        // Можно добавить данные для отчетов, если потребуется
        return "analytics_reports"; // thymeleaf шаблон analytics_reports.html
    }

    // Страница "Об авторе" (доступна для всех)
    @GetMapping("/about")
    public String about() {
        return "about"; // thymeleaf шаблон about.html
    }
}
