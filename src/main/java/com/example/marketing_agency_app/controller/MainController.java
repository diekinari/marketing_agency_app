package com.example.marketing_agency_app.controller;

import com.example.marketing_agency_app.model.AudienceSegment;
import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignMetrics;
import com.example.marketing_agency_app.model.Channel;
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


    private final CampaignService campaignService;

    private final ChannelService channelService;

    private final AudienceService audienceService;

    public MainController(AudienceService audienceService, CampaignService campaignService, ChannelService channelService) {
        this.audienceService = audienceService;
        this.campaignService = campaignService;
        this.channelService = channelService;
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
        return "campaign_form";
    }

    // Сохранение кампании (ADMIN)
    @PostMapping("/campaign/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCampaign(@ModelAttribute("campaign") Campaign campaign,
                               @RequestParam(name = "channelIds", required = false) List<Long> channelIds,
                               @RequestParam(name = "channelBudgets", required = false) List<Double> channelBudgets,
                               @RequestParam(name = "audienceIds", required = false) List<Long> audienceIds) {
        campaignService.saveCampaignWithRelations(campaign, channelIds, channelBudgets, audienceIds);
        return "redirect:/campaigns";
    }

    // Форма редактирования кампании (ADMIN)
    @GetMapping("/campaign/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCampaign(@PathVariable("id") Long id, Model model) {
        Campaign campaign = campaignService.findById(id);
        model.addAttribute("campaign", campaign);
        model.addAttribute("channels", channelService.findAll());
        model.addAttribute("audiences", audienceService.findAll());
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
    @GetMapping("/reports")
    public String reports(Model model) {
        return "analytics_reports";
    }

    // Страница "Об авторе"
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
