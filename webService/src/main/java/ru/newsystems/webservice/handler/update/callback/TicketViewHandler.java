package ru.newsystems.webservice.handler.update.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.callback.TicketViewDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.state.SerializableInlineType;
import ru.newsystems.webservice.config.cache.CacheStore;

import java.util.List;
import java.util.Optional;

import static ru.newsystems.webservice.utils.Telegram.Button.editedInlineKeyboard;
import static ru.newsystems.webservice.utils.Telegram.Button.prepareButtonsFromArticles;
import static ru.newsystems.webservice.utils.Telegram.Notification.queryIsMissing;

@Component
public class TicketViewHandler extends CallbackUpdateHandler<TicketViewDTO> {

    private final VirtaBot bot;
    private final CacheStore<TicketGetDTO> cache;

    public TicketViewHandler(VirtaBot bot, CacheStore<TicketGetDTO> cache) {
        this.bot = bot;
        this.cache = cache;
    }

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

        TicketGetDTO ticket = cache.get(update.getCallbackQuery().getMessage().getChatId());
        if (ticket != null) {
            bot.execute(SendChatAction
                    .builder()
                    .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                    .action(ActionType.TYPING.toString())
                    .build());

            Optional<TicketJ> first = ticket
                    .getTickets()
                    .stream()
                    .filter(e -> e.getTicketNumber().equals(dto.getTicketId()))
                    .findFirst();
            if (first.isPresent()) {
                List<Article> articles = first.get().getArticles();

                List<List<InlineKeyboardButton>> inlineKeyboard = prepareButtonsFromArticles(articles, 1, articles.size());

                bot.execute(EditMessageText
                        .builder()
                        .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                        .messageId(update.getCallbackQuery().getMessage().getMessageId())
                        .text("<pre>Количество сообщений по заявке № "
                                + dto.getTicketId()
                                + ": "
                                + articles.size()
                                + "</pre>")
                        .parseMode(ParseMode.HTML)
                        .build());
                editedInlineKeyboard(update, InlineKeyboardMarkup.builder().keyboard(inlineKeyboard), bot);
            } else {
                errorByQuery(update);
            }
        } else {
            errorByQuery(update);
        }


        System.out.println("articles");
    }

    private void errorByQuery(Update update) throws TelegramApiException {
        editedInlineKeyboard(update, InlineKeyboardMarkup.builder().clearKeyboard(), bot);
        queryIsMissing(update, bot);
    }
}
