package ru.newsystems.webservice.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.callback.TicketViewDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.dto.domain.TicketSearchDTO;
import ru.newsystems.basecore.model.state.Command;
import ru.newsystems.basecore.utils.StringUtil;
import ru.newsystems.webservice.config.cache.CacheStore;
import ru.newsystems.webservice.service.RestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ru.newsystems.webservice.utils.TelegramUtil.resultOperationToChat;

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
            cache.add(message.getChatId(), value);

            List<TicketJ> tickets = value.getTickets();
            List<List<InlineKeyboardButton>> inlineKeyboard = prepareButtons(tickets, 0, tickets.size());

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

        System.out.println("asd");
    }

    @Override
    public Command getCommand() {
        return Command.MY_TICKET;
    }


    private List<List<InlineKeyboardButton>> prepareButtons(List<TicketJ> tickets, int currentStartItem, int size) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        if (tickets.size() <= 3) {
            List<InlineKeyboardButton> collect = tickets.stream().map(ticket -> InlineKeyboardButton
                    .builder()
                    .text(String.valueOf(ticket))
                    .callbackData(StringUtil.serialize(
                            new TicketViewDTO(
                                    ticket.getTicketNumber(),
                                    currentStartItem,
                                    size
                            )))
                    .build()).collect(Collectors.toList());
            buttons.add(collect);

        }
        else if (tickets.size() == 4) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboard(tickets, 0, 2, currentStartItem, size);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboard(tickets, 2, 4, currentStartItem, size);
            buttons.add(firstRow);
            buttons.add(secondRow);
        }
        else if (tickets.size() == 5) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboard(tickets, 0, 2, currentStartItem, size);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboard(tickets, 2, 4, currentStartItem, size);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboard(tickets, 4, 5, currentStartItem, size);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        } else if (tickets.size() >= 6)  {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboard(tickets, 0, 2, currentStartItem, size);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboard(tickets, 2, 4, currentStartItem, size);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboard(tickets, 4, 6, currentStartItem, size);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        } else if (tickets.size() > 6) {

        }
        return buttons;
    }

    private List<InlineKeyboardButton> getListInlineKeyboard(List<TicketJ> tickets, int i, int i2, int currentStartItem, int size) {
        return IntStream
                .range(i, i2)
                .mapToObj(index -> InlineKeyboardButton
                        .builder()
                        .text(String.valueOf(tickets.get(index).getTicketNumber()))
                        .callbackData(
                                StringUtil.serialize(
                                        new TicketViewDTO(
                                                tickets.get(index).getTicketNumber(),
                                                currentStartItem,
                                                size
                                        )))
                        .build())
                .collect(Collectors.toList());
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
