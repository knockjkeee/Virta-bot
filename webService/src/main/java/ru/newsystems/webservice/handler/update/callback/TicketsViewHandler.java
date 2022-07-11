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
import ru.newsystems.webservice.utils.Telegram.Button;

import java.util.List;
import java.util.stream.Collectors;

import static ru.newsystems.webservice.utils.Telegram.Button.prepareButtons;

@Component
public class TicketsViewHandler extends CallbackUpdateHandler<TicketsViewDTO> {

    private final VirtaBot bot;
    private final CacheStore<TicketGetDTO> cache;

    public TicketsViewHandler(VirtaBot bot, CacheStore<TicketGetDTO> cache) {
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
            String direction = dto.getDirection();
            if (direction.equals("to")) {
                List<TicketJ> tickets = ticket
                        .getTickets()
                        .stream()
                        .skip((long) (dto.getPage() * Button.COUNT_ITEM_IN_PAGE))
                        .collect(Collectors.toList());

                List<List<InlineKeyboardButton>> inlineKeyboard = prepareButtons(tickets, dto.getPage() + 1, ticket
                        .getTickets()
                        .size());

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
                        .skip((long) ((dto.getPage() - 1) * Button.COUNT_ITEM_IN_PAGE - Button.COUNT_ITEM_IN_PAGE))
                        .collect(Collectors.toList());

                List<List<InlineKeyboardButton>> inlineKeyboard = prepareButtons(tickets, dto.getPage() - 1, ticket
                        .getTickets()
                        .size());

                bot.execute(EditMessageReplyMarkup
                        .builder()
                        .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(inlineKeyboard).build())
                        .build());
            }
        }
    }
}
