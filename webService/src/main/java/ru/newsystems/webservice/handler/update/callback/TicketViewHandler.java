package ru.newsystems.webservice.handler.update.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.model.dto.callback.TicketViewDTO;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Component
public class TicketViewHandler extends CallbackUpdateHandler<TicketViewDTO>{
    @Override
    protected Class<TicketViewDTO> getDtoType() {
        return TicketViewDTO.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.TICKET_VIEW;
    }

    @Override
    protected void handleCallback(Update update, TicketViewDTO dto) throws TelegramApiException {
        System.out.println("sad");
    }
}
