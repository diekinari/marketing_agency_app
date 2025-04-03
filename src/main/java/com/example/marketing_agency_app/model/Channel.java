package com.example.marketing_agency_app.model;


import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "channel")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Long channelId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Если необходимо, можно добавить обратную связь с CampaignChannel
    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CampaignChannel> campaignChannels;

    // Getters и setters
    // ...

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CampaignChannel> getCampaignChannels() {
        return campaignChannels;
    }

    public void setCampaignChannels(Set<CampaignChannel> campaignChannels) {
        this.campaignChannels = campaignChannels;
    }
}

