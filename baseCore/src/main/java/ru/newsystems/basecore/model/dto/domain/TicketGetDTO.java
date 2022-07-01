package ru.newsystems.basecore.model.dto.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.model.domain.Error;
import ru.newsystems.basecore.model.domain.TicketJ;

import java.util.List;

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketGetDTO {
    @JsonProperty("Error")
    private Error error;
    @JsonProperty("Ticket")
    private List<TicketJ> tickets;
}
