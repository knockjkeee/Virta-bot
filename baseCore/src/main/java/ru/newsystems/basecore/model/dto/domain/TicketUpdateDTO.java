package ru.newsystems.basecore.model.dto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketUpdateDTO {
    @JsonProperty("ArticleID")
    private Long articleID;
    @JsonProperty("TicketID")
    private Long ticketID;
    @JsonProperty("TicketID")
    private Long ticketNumber;
}
