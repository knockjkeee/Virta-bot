package ru.newsystems.basecore.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.model.domain.TickerJ;

import java.util.List;

@Data
@RequiredArgsConstructor
public class TicketDTOJ {
    @JsonProperty("Ticket")
    private List<TickerJ> ticket;
}
