package com.example.marketing_agency_app.service;


import com.example.marketing_agency_app.model.Channel;
import com.example.marketing_agency_app.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
