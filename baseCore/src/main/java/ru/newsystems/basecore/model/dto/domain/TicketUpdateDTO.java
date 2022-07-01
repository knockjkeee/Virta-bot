package ru.newsystems.basecore.model.dto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.model.domain.Error;

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketUpdateDTO {
    @JsonProperty("Error")
    private Error error;
    @JsonProperty("ArticleID")
    private String articleID;
    @JsonProperty("TicketID")
    private String ticketID;
    @JsonProperty("TicketNumber")
    private Long ticketNumber;
}
