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
    public static final double COUNT_ITEM_IN_PAGE = 6.0;

    public static List<List<InlineKeyboardButton>> prepareButtons(List<TicketJ> tickets, int page, int fullSize) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        prepareRowButton(tickets, buttons);
//        if (isHome) {
//            buttons.add(List.of(InlineKeyboardButton
//                    .builder()
//                    .text(ReplyKeyboardButton.HOME.getLabel())
//                    .callbackData(StringUtil.serialize(new TicketsViewDTO(page + 1, tickets.size(),"to")))
//                    .build()));
//        }
        prepareNavigationButton(tickets, page, buttons, fullSize);
        return buttons;
    }

    public static double getAllPages(int size) {
        return Math.ceil((double) size / COUNT_ITEM_IN_PAGE);
    }

    private static void prepareNavigationButton(List<TicketJ> tickets, int page, List<List<InlineKeyboardButton>> buttons, int fullSizeData) {
        if (fullSizeData > 6) {
            double allPages = getAllPages(fullSizeData);
            if (page == 1) {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.TO.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsViewDTO(page, tickets.size(), "to")))
                        .build()));
            } else if (allPages == page) {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.BACK.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsViewDTO(page, tickets.size(), "back")))
                        .build()));
            } else {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.BACK.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsViewDTO(page, tickets.size(), "back")))
                        .build(), InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.TO.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsViewDTO(page, tickets.size(), "to")))
                        .build()));
            }
        }
    }

    private static void prepareRowButton(List<TicketJ> tickets, List<List<InlineKeyboardButton>> buttons) {
        if (tickets.size() <= 3) {
            tickets.forEach(ticket -> buttons.add(List.of(InlineKeyboardButton
                    .builder()
                    .text(ticket.getTicketNumber())
                    .callbackData(StringUtil.serialize(new ArticlesViewDTO(ticket.getTicketNumber())))
                    .build())));


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
