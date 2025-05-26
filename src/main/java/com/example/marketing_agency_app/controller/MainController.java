package com.example.marketing_agency_app.controller;

import com.example.marketing_agency_app.model.AudienceSegment;
import com.example.marketing_agency_app.model.Campaign;
import com.example.marketing_agency_app.model.CampaignMetrics;
import com.example.marketing_agency_app.model.Channel;
import com.example.marketing_agency_app.service.AudienceService;
import com.example.marketing_agency_app.service.CampaignService;
import com.example.marketing_agency_app.service.ChannelService;
import com.example.marketing_agency_app.service.ReportsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Главный контроллер приложения.
 * Обрабатывает CRUD-операции для кампаний, каналов, сегментов аудитории,
 * а также отображение аналитики и отчётов.
 */
@Controller
public class MainController {

    private final CampaignService campaignService;
    private final ChannelService channelService;
    private final AudienceService audienceService;
    private final ReportsService reportsService;
    private final ObjectMapper objectMapper;

    /**
     * Конструктор для MainController.
     *
     * @param audienceService   сервис по работе с сегментами аудитории
     * @param campaignService   сервис по работе с кампаниями
     * @param channelService    сервис по работе с каналами
     * @param reportsService    сервис формирования аналитики и отчётов
     * @param objectMapper      Jackson ObjectMapper для работы с JSON
     */
    public MainController(AudienceService audienceService,
                          CampaignService campaignService,
                          ChannelService channelService,
                          ReportsService reportsService,
                          ObjectMapper objectMapper) {
        this.audienceService = audienceService;
        this.campaignService = campaignService;
        this.channelService = channelService;
        this.reportsService = reportsService;
        this.objectMapper = objectMapper;
    }

    // ------------------ Кампании ------------------

    /**
     * Отображает дашборд со списком кампаний и позволяет фильтровать/сортировать.
     *
     * @param model      модель MVC
     * @param request    HTTP-запрос (для получения URI)
     * @param name       фильтр по названию кампании (частичное совпадение)
     * @param startDate  фильтр по дате начала (ISO-формат)
     * @param endDate    фильтр по дате окончания (ISO-формат)
     * @param status     фильтр по статусу кампании
     * @param channelId  фильтр по каналу
     * @param minBudget  минимальный бюджет (в условных единицах)
     * @param maxBudget  максимальный бюджет (в условных единицах)
     * @param minRoi     минимальный ROI (%)
     * @param maxRoi     максимальный ROI (%)
     * @param sortField  поле для сортировки
     * @param sortDir    направление сортировки ("asc" или "desc")
     * @return           имя Thymeleaf-шаблона "dashboard"
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model,
                            HttpServletRequest request,
                            @RequestParam(value = "name",       required = false) String name,
                            @RequestParam(value = "startDate",  required = false) String startDate,
                            @RequestParam(value = "endDate",    required = false) String endDate,
                            @RequestParam(value = "status",     required = false) String status,
                            @RequestParam(value = "channelId",  required = false) Long channelId,
                            @RequestParam(value = "minBudget",  required = false) BigDecimal minBudget,
                            @RequestParam(value = "maxBudget",  required = false) BigDecimal maxBudget,
                            @RequestParam(value = "minRoi",     required = false) BigDecimal minRoi,
                            @RequestParam(value = "maxRoi",     required = false) BigDecimal maxRoi,
                            @RequestParam(value = "sortField",  defaultValue = "startDate") String sortField,
                            @RequestParam(value = "sortDir",    defaultValue = "asc")       String sortDir) {

        // Настройка сортировки
        Sort sort = Sort.by(sortField);
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();

        // Получаем отфильтрованный список метрик кампаний
        List<CampaignMetrics> campaigns = campaignService.findFilteredCampaignMetrics(
                name,
                startDate,
                endDate,
                status,
                channelId,
                minBudget,
                maxBudget,
                minRoi,
                maxRoi,
                sort
        );

        // Подготовка данных для Thymeleaf
        model.addAttribute("campaigns",       campaigns);
        model.addAttribute("channels",        channelService.findAll());     // для выпадающего списка каналов
        model.addAttribute("requestURI",      request.getRequestURI());
        model.addAttribute("name",            name);
        model.addAttribute("startDate",       startDate);
        model.addAttribute("endDate",         endDate);
        model.addAttribute("status",          status);
        model.addAttribute("channelId",       channelId);
        model.addAttribute("minBudget",       minBudget);
        model.addAttribute("maxBudget",       maxBudget);
        model.addAttribute("minRoi",          minRoi);
        model.addAttribute("maxRoi",          maxRoi);
        model.addAttribute("sortField",       sortField);
        model.addAttribute("sortDir",         sortDir);
        model.addAttribute("reverseSortDir",  "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");

        return "dashboard";
    }


    /**
     * Отображает список всех кампаний.
     *
     * @param model   модель MVC
     * @param request HTTP-запрос
     * @return        имя Thymeleaf-шаблона "campaigns"
     */
    @GetMapping("/campaigns")
    public String campaigns(Model model, HttpServletRequest request) {
        List<Campaign> campaigns = campaignService.findAll();
        model.addAttribute("campaigns",  campaigns);
        model.addAttribute("requestURI", request.getRequestURI());
        return "campaigns";
    }

    /**
     * Форма создания новой кампании (доступно ADMIN).
     *
     * @param model модель MVC
     * @return      имя Thymeleaf-шаблона "campaign_form"
     */
    @GetMapping("/campaign/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newCampaign(Model model) {
        model.addAttribute("campaign",         new Campaign());
        model.addAttribute("channels",         channelService.findAll());
        model.addAttribute("audiences",        audienceService.findAll());
        model.addAttribute("availableChannels", channelService.findAll());
        return "campaign_form";
    }

    /**
     * Сохраняет новую или отредактированную кампанию вместе с каналами и аудиториями (доступно ADMIN).
     *
     * @param campaign                   объект Campaign из формы
     * @param channelIds                 id существующих каналов
     * @param channelBudgets             бюджеты по существующим каналам
     * @param channelImpressions         показы по существующим каналам
     * @param channelClicks              клики по существующим каналам
     * @param channelConversions         конверсии по существующим каналам
     * @param channelSpentAmounts        потраченные суммы по существующим каналам
     * @param newChannelIds              id новых каналов
     * @param newChannelBudgets          бюджеты по новым каналам
     * @param newChannelImpressions      показы по новым каналам
     * @param newChannelClicks           клики по новым каналам
     * @param newChannelConversions      конверсии по новым каналам
     * @param newChannelSpentAmounts     потраченные суммы по новым каналам
     * @param deletedChannelIds          перечисленные через запятую id удалённых каналов
     * @param audienceIds                id сегментов аудитории
     * @return                           редирект на список кампаний
     */
    @PostMapping("/campaign/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveCampaign(
            @ModelAttribute("campaign") Campaign campaign,
            @RequestParam(name = "channelIds",             required = false) List<Long>      channelIds,
            @RequestParam(name = "channelBudgets",         required = false) List<Double>    channelBudgets,
            @RequestParam(name = "channelImpressions",     required = false) List<Integer>   channelImpressions,
            @RequestParam(name = "channelClicks",          required = false) List<Integer>   channelClicks,
            @RequestParam(name = "channelConversions",     required = false) List<Integer>   channelConversions,
            @RequestParam(name = "channelSpentAmounts",    required = false) List<BigDecimal> channelSpentAmounts,
            @RequestParam(name = "newChannelIds",          required = false) List<Long>      newChannelIds,
            @RequestParam(name = "newChannelBudgets",      required = false) List<Double>    newChannelBudgets,
            @RequestParam(name = "newChannelImpressions",  required = false) List<Integer>   newChannelImpressions,
            @RequestParam(name = "newChannelClicks",       required = false) List<Integer>   newChannelClicks,
            @RequestParam(name = "newChannelConversions",  required = false) List<Integer>   newChannelConversions,
            @RequestParam(name = "newChannelSpentAmounts", required = false) List<BigDecimal> newChannelSpentAmounts,
            @RequestParam(name = "deletedChannelIds",      required = false) String          deletedChannelIds,
            @RequestParam(name = "audienceIds",            required = false) List<Long>      audienceIds
    ) {
        campaignService.saveCampaignWithRelations(
                campaign,
                channelIds, channelBudgets, channelImpressions, channelClicks, channelConversions, channelSpentAmounts,
                newChannelIds, newChannelBudgets, newChannelImpressions, newChannelClicks, newChannelConversions, newChannelSpentAmounts,
                deletedChannelIds, audienceIds
        );
        return "redirect:/campaigns";
    }

    /**
     * Форма редактирования кампании (доступно ADMIN).
     *
     * @param id    идентификатор кампании
     * @param model модель MVC
     * @return      имя шаблона "campaign_form" или редирект, если не найдено
     */
    @GetMapping("/campaign/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editCampaign(@PathVariable("id") Long id, Model model) {
        Campaign campaign = campaignService.findById(id);
        campaign.setAudienceIds(
                campaign.getCampaignAudiences().stream()
                        .map(ca -> ca.getAudience().getAudienceId())
                        .collect(Collectors.toList())
        );
        model.addAttribute("campaign",          campaign);
        model.addAttribute("channels",          channelService.findAll());
        model.addAttribute("audiences",         audienceService.findAll());
        model.addAttribute("availableChannels", channelService.findAvailableChannels(campaign.getCampaignChannels()));
        return "campaign_form";
    }

    /**
     * Удаляет кампанию по id (доступно ADMIN).
     *
     * @param id идентификатор кампании
     * @return   редирект на список кампаний
     */
    @GetMapping("/campaign/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCampaign(@PathVariable("id") Long id) {
        campaignService.delete(id);
        return "redirect:/campaigns";
    }

    // ------------------ Каналы ------------------

    /**
     * Отображает список всех каналов.
     *
     * @param model   модель MVC
     * @param request HTTP-запрос
     * @return        имя шаблона "channels_management"
     */
    @GetMapping("/channels")
    public String channels(Model model, HttpServletRequest request) {
        List<Channel> channels = channelService.findAll();
        model.addAttribute("channels",  channels);
        model.addAttribute("requestURI", request.getRequestURI());
        return "channels_management";
    }

    /**
     * Форма создания нового канала (доступно ADMIN).
     *
     * @param model модель MVC
     * @return      имя шаблона "channel_form"
     */
    @GetMapping("/channel/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newChannel(Model model) {
        model.addAttribute("channel", new Channel());
        return "channel_form";
    }

    /**
     * Сохраняет новый или отредактированный канал (доступно ADMIN).
     *
     * @param channel объект Channel из формы
     * @return        редирект на список каналов
     */
    @PostMapping("/channel/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveChannel(@ModelAttribute("channel") Channel channel) {
        channelService.save(channel);
        return "redirect:/channels";
    }

    /**
     * Форма редактирования канала (доступно ADMIN).
     *
     * @param id    идентификатор канала
     * @param model модель MVC
     * @return      имя шаблона "channel_form" или редирект, если не найден
     */
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

    /**
     * Удаляет канал по id (доступно ADMIN).
     *
     * @param id идентификатор канала
     * @return   редирект на список каналов
     */
    @GetMapping("/channel/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteChannel(@PathVariable("id") Long id) {
        channelService.delete(id);
        return "redirect:/channels";
    }

    // ------------------ Аудитории ------------------

    /**
     * Отображает список сегментов аудитории.
     *
     * @param model   модель MVC
     * @param request HTTP-запрос
     * @return        имя шаблона "audience_segments_management"
     */
    @GetMapping("/audiences")
    public String audiences(Model model, HttpServletRequest request) {
        List<AudienceSegment> audiences = audienceService.findAll();
        model.addAttribute("audiences",  audiences);
        model.addAttribute("requestURI", request.getRequestURI());
        return "audience_segments_management";
    }

    /**
     * Форма создания нового сегмента аудитории (доступно ADMIN).
     *
     * @param model модель MVC
     * @return      имя шаблона "audience_form"
     */
    @GetMapping("/audience/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newAudience(Model model) {
        model.addAttribute("audience", new AudienceSegment());
        return "audience_form";
    }

    /**
     * Блокирует поле demographics от автоматического биндинга.
     *
     * @param binder биндер данных формы
     */
    @InitBinder("audience")
    protected void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("demographics");
    }

    /**
     * Сохраняет сегмент аудитории, парся JSON демографии (доступно ADMIN).
     *
     * @param audience        объект AudienceSegment из формы
     * @param demographicsJson текст JSON из формы
     * @param model           модель для ошибок
     * @return                редирект или возврат формы при ошибке
     */
    @PostMapping("/audience/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveAudience(
            @ModelAttribute("audience") AudienceSegment audience,
            @RequestParam("demographics") String demographicsJson,
            Model model
    ) {
        JsonNode demoNode;
        if (demographicsJson != null && !demographicsJson.trim().isEmpty()) {
            try {
                demoNode = objectMapper.readTree(demographicsJson);
            } catch (JsonProcessingException e) {
                model.addAttribute("jsonError", "Некорректный JSON: " + e.getOriginalMessage());
                return "audience_form";
            }
        } else {
            demoNode = objectMapper.createObjectNode();
        }
        audience.setDemographics(demoNode);
        audienceService.save(audience);
        return "redirect:/audiences";
    }

    /**
     * Форма редактирования сегмента аудитории (доступно ADMIN).
     *
     * @param id    идентификатор сегмента
     * @param model модель MVC
     * @return      имя шаблона "audience_form" или редирект, если не найден
     */
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

    /**
     * Удаляет сегмент аудитории по id (доступно ADMIN).
     *
     * @param id идентификатор сегмента
     * @return   редирект на список сегментов
     */
    @GetMapping("/audience/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAudience(@PathVariable("id") Long id) {
        audienceService.delete(id);
        return "redirect:/audiences";
    }

    // ------------------ Отчёты и информация ------------------

    /**
     * Отображает страницу аналитики и отчётов с распределением метрик кампании по дням.
     *
     * @param model      модель MVC
     * @param campaignId опциональный id выбранной кампании
     * @param startStr   строка начала периода
     * @param endStr     строка конца периода
     * @return           имя шаблона "analytics_reports"
     */
    @GetMapping("/reports")
    public String reports(
            Model model,
            @RequestParam(value = "campaignId", required = false) Long campaignId,
            @RequestParam(value = "startDate",   required = false) String startStr,
            @RequestParam(value = "endDate",     required = false) String endStr
    ) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start;
        LocalDate end;
        if (campaignId != null
                && (startStr == null || startStr.isEmpty())
                && (endStr == null   || endStr.isEmpty())) {
            Campaign c = campaignService.findById(campaignId);
            start = c.getStartDate();
            end   = c.getEndDate();
        } else {
            start = (startStr != null && !startStr.isEmpty())
                    ? LocalDate.parse(startStr, dtf)
                    : LocalDate.now().minusMonths(1);
            end   = (endStr   != null && !endStr.isEmpty())
                    ? LocalDate.parse(endStr, dtf)
                    : LocalDate.now();
        }

        List<Campaign> campaigns = campaignService.findAll();
        model.addAttribute("campaigns",          campaigns);
        model.addAttribute("selectedCampaignId", campaignId);

        Map<String, Object> data = reportsService.getDailyMetrics(campaignId, start, end);
        model.addAttribute("chartLabels",     data.get("labels"));
        model.addAttribute("impressionsData", data.get("impressions"));
        model.addAttribute("clicksData",      data.get("clicks"));
        model.addAttribute("conversionsData", data.get("conversions"));
        model.addAttribute("spentData",       data.get("spent"));
        model.addAttribute("startDate",       startStr);
        model.addAttribute("endDate",         endStr);

        return "analytics_reports";
    }

    /**
     * Отображает страницу "Об авторе".
     *
     * @return имя шаблона "about"
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
