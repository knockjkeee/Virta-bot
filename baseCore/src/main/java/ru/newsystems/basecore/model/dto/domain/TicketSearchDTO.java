package ru.newsystems.basecore.model.dto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.model.domain.Error;

import java.util.List;

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketSearchDTO {
    @JsonProperty("Error")
    private Error error;
    @JsonProperty("TicketID")
    private List<Long> ticketIDs;
}
