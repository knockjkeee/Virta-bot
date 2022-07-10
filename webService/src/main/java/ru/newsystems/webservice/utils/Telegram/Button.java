package ru.newsystems.webservice.utils.Telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.callback.ArticlesViewDTO;
import ru.newsystems.basecore.model.dto.callback.TicketsViewDTO;
import ru.newsystems.basecore.model.state.ReplyKeyboardButton;
import ru.newsystems.basecore.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Button {

    public static List<List<InlineKeyboardButton>> prepareButtons(Long msgId, List<TicketJ> tickets, int page, boolean isHome) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        if (isHome) {
            buttons.add(List.of(InlineKeyboardButton
                    .builder()
                    .text(ReplyKeyboardButton.HOME.getLabel())
                    .callbackData(StringUtil.serialize(new TicketsViewDTO(msgId, page, tickets.size(), true)))
                    .build()));
        }
        prepareRowButton(tickets, buttons);
        prepareNavigationButton(msgId, tickets, page, buttons, isHome);
        return buttons;
    }

    private static void prepareNavigationButton(Long msgId, List<TicketJ> tickets, int page, List<List<InlineKeyboardButton>> buttons, boolean isHome) {
        if (tickets.size() > 6) {
            buttons.add(List.of(InlineKeyboardButton
                    .builder()
                    .text(ReplyKeyboardButton.TO.getLabel())
                    .callbackData(StringUtil.serialize(new TicketsViewDTO(msgId, page, tickets.size(), isHome)))
                    .build()));
        }
    }

    private static void prepareRowButton(List<TicketJ> tickets, List<List<InlineKeyboardButton>> buttons) {
        if (tickets.size() <= 3) {
            List<InlineKeyboardButton> collect = tickets
                    .stream()
                    .map(ticket -> InlineKeyboardButton
                            .builder()
                            .text(String.valueOf(ticket))
                            .callbackData(StringUtil.serialize(new ArticlesViewDTO(ticket.getTicketNumber())))
                            .build())
                    .collect(Collectors.toList());
            buttons.add(collect);

        } else if (tickets.size() == 4) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboard(tickets, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboard(tickets, 2, 4);
            buttons.add(firstRow);
            buttons.add(secondRow);
        } else if (tickets.size() == 5) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboard(tickets, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboard(tickets, 2, 4);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboard(tickets, 4, 5);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        } else {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboard(tickets, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboard(tickets, 2, 4);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboard(tickets, 4, 6);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        }
    }

    private static List<InlineKeyboardButton> getListInlineKeyboard(List<TicketJ> tickets, int start, int end) {
        return IntStream
                .range(start, end)
                .mapToObj(index -> InlineKeyboardButton
                        .builder()
                        .text(String.valueOf(tickets.get(index).getTicketNumber()))
                        .callbackData(StringUtil.serialize(new ArticlesViewDTO(tickets.get(index).getTicketNumber())))
                        .build())
                .collect(Collectors.toList());
    }
}
