package com.example.marketing_agency_app.service;


import com.example.marketing_agency_app.model.CampaignChannel;
import com.example.marketing_agency_app.model.Channel;
import com.example.marketing_agency_app.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    public Channel findById(Long id) {
        return channelRepository.findById(id).orElse(null);
    }

    public Channel save(Channel channel) {
        return channelRepository.save(channel);
    }

    public void delete(Long id) {
        channelRepository.deleteById(id);
    }

    public List<Channel> findAvailableChannels(Collection<CampaignChannel> existingCampaignChannels) {
        // Получаем список всех каналов из базы
        List<Channel> allChannels = channelRepository.findAll();
        // Извлекаем идентификаторы каналов, которые уже привязаны к кампании
        Set<Long> usedChannelIds = existingCampaignChannels.stream()
                .map(cc -> cc.getChannel().getChannelId())
                .collect(Collectors.toSet());
        // Возвращаем те каналы, идентификаторы которых отсутствуют в списке использованных
        return allChannels.stream()
                .filter(channel -> !usedChannelIds.contains(channel.getChannelId()))
                .collect(Collectors.toList());
    }
}
