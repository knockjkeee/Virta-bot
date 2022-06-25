package ru.newsystems.basecore.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.state.MessageState;

@Data
@RequiredArgsConstructor
public class MessageUpdateDTO {
    TicketJ ticket;
    MessageState state;
}
