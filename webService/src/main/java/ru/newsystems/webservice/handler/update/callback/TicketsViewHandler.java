package ru.newsystems.webservice.handler.update.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.callback.TicketsViewDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.state.SerializableInlineType;
import ru.newsystems.webservice.config.cache.CacheStore;
import ru.newsystems.webservice.service.RestService;
import ru.newsystems.webservice.utils.Telegram.Button;

import java.util.List;
import java.util.stream.Collectors;

import static ru.newsystems.webservice.utils.Telegram.Button.getAllPages;
import static ru.newsystems.webservice.utils.Telegram.Button.prepareButtons;

@Component
public class TicketsViewHandler extends CallbackUpdateHandler<TicketsViewDTO> {

    public static final double COUNT_ITEM_IN_PAGE = 6.0;
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

        TicketGetDTO ticket = cache.get(update.getCallbackQuery().getMessage().getChatId());
        if (ticket != null) {
            double allPages = getAllPages(dto.getSize());
            String direction = dto.getDirection();
            if (direction.equals("to")) {
                List<TicketJ> tickets = ticket
                        .getTickets()
                        .stream()
                        .skip((long) (dto.getPage() * Button.COUNT_ITEM_IN_PAGE))
                        .collect(Collectors.toList());
                List<List<InlineKeyboardButton>> inlineKeyboard = prepareButtons(update
                        .getCallbackQuery()
                        .getMessage()
                        .getChatId(), tickets, dto.getPage() + 1, false, ticket.getTickets().size());

                bot.execute(EditMessageReplyMarkup
                        .builder()
                        .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(inlineKeyboard).build())
                        .build());

            }
            if (direction.equals("back")) {
                List<TicketJ> tickets = ticket
                        .getTickets()
                        .stream()
                        .skip((long) ((dto.getPage() - 1) * Button.COUNT_ITEM_IN_PAGE))
                        .collect(Collectors.toList());
                List<List<InlineKeyboardButton>> inlineKeyboard = prepareButtons(update
                        .getCallbackQuery()
                        .getMessage()
                        .getChatId(), tickets, dto.getPage() - 1, false, ticket.getTickets().size());

                bot.execute(EditMessageReplyMarkup
                        .builder()
                        .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(inlineKeyboard).build())
                        .build());
            }


//            ticket.getTickets().stream().skip()
        }

        System.out.println("tickets");
    }
}
