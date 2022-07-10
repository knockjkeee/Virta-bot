package ru.newsystems.webservice.handler.update.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.callback.TicketsViewDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.state.SerializableInlineType;
import ru.newsystems.webservice.config.cache.CacheStore;
import ru.newsystems.webservice.service.RestService;

@Component
public class TicketsViewHandler extends CallbackUpdateHandler<TicketsViewDTO>{

    private final RestService restService;
    private final VirtaBot bot;
    private final CacheStore<TicketGetDTO> cache;

    public TicketsViewHandler(RestService restService, VirtaBot bot, CacheStore<TicketGetDTO> cache) {
        this.restService = restService;
        this.bot = bot;
        this.cache = cache;
    }


    @Override
    protected Class<TicketsViewDTO> getDtoType() {
        return TicketsViewDTO.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.TICKETS_VIEW;
    }

    @Override
    protected void handleCallback(Update update, TicketsViewDTO dto) throws TelegramApiException {
        System.out.println("tickets");
    }
}
