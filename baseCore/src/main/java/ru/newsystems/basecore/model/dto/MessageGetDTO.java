package ru.newsystems.basecore.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.state.MessageState;

@Data
@RequiredArgsConstructor
public class MessageGetDTO {
    TicketJ ticket;
    MessageState state;
}
