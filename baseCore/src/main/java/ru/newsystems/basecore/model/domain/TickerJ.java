package ru.newsystems.basecore.model.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickerJ {

    @JsonProperty("TicketID")
    private Long ticketIDJ;

    @JsonProperty("TicketNumber")
    private String ticketNumberJ;

    @JsonProperty("Title")
    private String titleJ;

    @JsonProperty("ArchiveFlag")
    private String archiveFlagJ;

    @JsonProperty("Age")
    private String ageJ;

    @JsonProperty("CustomerID")
    private String customerIdJ;

    @JsonProperty("CustomerUserID")
    private String customerUserJ;

    @JsonProperty("Lock")
    private String lockJ;

    @JsonProperty("Queue")
    private String queueJ;

    @JsonProperty("Type")
    private String typeJ;

    @JsonProperty("StateType")
    private String stateTypeJ;

    @JsonProperty("Priority")
    private String priorityJ;

    @JsonProperty("Article")
    private List<Article> articles;
}
