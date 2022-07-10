package ru.newsystems.webservice.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.dto.domain.TicketSearchDTO;
import ru.newsystems.basecore.model.state.Command;
import ru.newsystems.webservice.config.cache.CacheStore;
import ru.newsystems.webservice.service.RestService;

import java.util.List;
import java.util.Optional;

import static ru.newsystems.webservice.utils.Telegram.Button.prepareButtons;
import static ru.newsystems.webservice.utils.Telegram.Notification.resultOperationToChat;

@Component
public class MyTicketsCommandHandler implements CommandHandler {

    private final RestService restService;
    private final VirtaBot bot;
    private final CacheStore<TicketGetDTO> cache;

    public MyTicketsCommandHandler(RestService restService, VirtaBot bot, CacheStore<TicketGetDTO> cache) {
        this.restService = restService;
        this.bot = bot;
        this.cache = cache;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        Optional<TicketSearchDTO> ticketOperationSearch = restService.getTicketOperationSearch();

        if (ticketOperationSearch.isPresent()
                && ticketOperationSearch.get().getTicketIDs() != null
                && ticketOperationSearch.get().getTicketIDs().size() > 0) {
            List<Long> ticketIDs = ticketOperationSearch.get().getTicketIDs();
            Optional<TicketGetDTO> ticketOperationGet = restService.getTicketOperationGet(ticketIDs);
            TicketGetDTO value = ticketOperationGet.get();
            cache.update(message.getChatId(), value);
            List<List<InlineKeyboardButton>> inlineKeyboard = prepareButtons(message.getChatId(), value.getTickets(), 0, false);

            bot.execute(SendMessage
                    .builder()
                    .chatId(String.valueOf(message.getChatId()))
                    .text("<pre>Количество открытых заявок: <b>" + ticketIDs.size() + "</b></pre>")
                    .parseMode(ParseMode.HTML)
                    .protectContent(true)
                    .replyToMessageId(message.getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(inlineKeyboard).build())
                    .build());

        } else {
            resultOperationToChat(message, bot, false);
        }
    }

    @Override
    public Command getCommand() {
        return Command.MY_TICKET;
    }





//    List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
//        buttons.add(List.of(InlineKeyboardButton
//                .builder()
//                .text("Отправить комментарий")
//                .callbackData(StringUtil.serialize(new SendDataDTO(ticket.getTicketNumber())))
//            .build()));
//        buttons.add(List.of(InlineKeyboardButton
//                .builder()
//                .text("Выгрузить документы")
//                .callbackData(StringUtil.serialize(new DownloadFilesDTO(ticket.getTicketNumber())))
//            .build()));

}
