package ru.newsystems.webservice.utils.Telegram;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.callback.*;
import ru.newsystems.basecore.model.state.ReplyKeyboardButton;
import ru.newsystems.basecore.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Button {
    public static final double COUNT_ITEM_IN_PAGE = 6.0;

    public static double getAllPages(int size) {
        return Math.ceil((double) size / COUNT_ITEM_IN_PAGE);
    }

    public static List<List<InlineKeyboardButton>> prepareButtonsFromTickets(List<TicketJ> tickets, int page, int fullSize) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        prepareRowButtonFromTickets(tickets, buttons);
//        if (isHome) {
//            buttons.add(List.of(InlineKeyboardButton
//                    .builder()
//                    .text(ReplyKeyboardButton.HOME.getLabel())
//                    .callbackData(StringUtil.serialize(new TicketsViewDTO(page + 1, tickets.size(),"to")))
//                    .build()));
//        }
        prepareNavigationButtonFromTickets(page, buttons, fullSize);
        return buttons;
    }

    public static List<List<InlineKeyboardButton>> prepareButtonsFromArticles(List<Article> articles, int page, int fullSize) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        prepareRowButtonFromArticles(articles, buttons);
//        if (isHome) {
//            buttons.add(List.of(InlineKeyboardButton
//                    .builder()
//                    .text(ReplyKeyboardButton.HOME.getLabel())
//                    .callbackData(StringUtil.serialize(new TicketsViewDTO(page + 1, tickets.size(),"to")))
//                    .build()));
//        }
        prepareNavigationButtonFromArticles(articles, page, buttons, fullSize);
        return buttons;
    }


    private static void prepareNavigationButtonFromTickets(int page, List<List<InlineKeyboardButton>> buttons, int fullSizeData) {
        if (fullSizeData > 6) {
            double allPages = getAllPages(fullSizeData);
            if (page == 1) {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.TO.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsNavigationViewDTO(page, "to")))
                        .build()));
            } else if (allPages == page) {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.BACK.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsNavigationViewDTO(page, "back")))
                        .build()));
            } else {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.BACK.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsNavigationViewDTO(page, "back")))
                        .build(), InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.TO.getLabel())
                        .callbackData(StringUtil.serialize(new TicketsNavigationViewDTO(page, "to")))
                        .build()));
            }
        }
    }

    private static void prepareNavigationButtonFromArticles(List<Article> articles, int page, List<List<InlineKeyboardButton>> buttons, int fullSizeData) {
        buttons.add(List.of(InlineKeyboardButton
                .builder()
                .text(ReplyKeyboardButton.HOME.getLabel() + "Заявок")
                .callbackData(StringUtil.serialize(new TicketsHomeViewDTO(0)))
                .build()));
        if (fullSizeData > 6) {
            double allPages = getAllPages(fullSizeData);
            if (page == 1) {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.TO.getLabel())
                        .callbackData(StringUtil.serialize(new ArticlesNavigationViewDTO(page, articles.size(), "to")))
                        .build()));
            } else if (allPages == page) {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.BACK.getLabel())
                        .callbackData(StringUtil.serialize(new ArticlesNavigationViewDTO(page, articles.size(), "back")))
                        .build()));
            } else {
                buttons.add(List.of(InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.BACK.getLabel())
                        .callbackData(StringUtil.serialize(new ArticlesNavigationViewDTO(page, articles.size(), "back")))
                        .build(), InlineKeyboardButton
                        .builder()
                        .text(ReplyKeyboardButton.TO.getLabel())
                        .callbackData(StringUtil.serialize(new ArticlesNavigationViewDTO(page, articles.size(), "to")))
                        .build()));
            }
        }
    }

    private static void prepareRowButtonFromTickets(List<TicketJ> tickets, List<List<InlineKeyboardButton>> buttons) {
        if (tickets.size() <= 3) {
            tickets.forEach(ticket -> buttons.add(List.of(InlineKeyboardButton
                    .builder()
                    .text(ticket.getTicketNumber())
                    .callbackData(StringUtil.serialize(new TicketViewDTO(ticket.getTicketNumber(), 0)))
                    .build())));
        } else if (tickets.size() == 4) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboardFromTickets(tickets, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboardFromTickets(tickets, 2, 4);
            buttons.add(firstRow);
            buttons.add(secondRow);
        } else if (tickets.size() == 5) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboardFromTickets(tickets, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboardFromTickets(tickets, 2, 4);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboardFromTickets(tickets, 4, 5);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        } else {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboardFromTickets(tickets, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboardFromTickets(tickets, 2, 4);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboardFromTickets(tickets, 4, 6);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        }
    }

    private static void prepareRowButtonFromArticles(List<Article> articles, List<List<InlineKeyboardButton>> buttons) {
        if (articles.size() <= 3) {
            articles.forEach(article -> buttons.add(List.of(InlineKeyboardButton
                    .builder()
                    .text(String.valueOf(article.getArticleID()))
                    .callbackData(StringUtil.serialize(new ArticleViewDTO(String.valueOf(article.getArticleID()), 0)))
                    .build())));
        } else if (articles.size() == 4) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboardFromArticles(articles, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboardFromArticles(articles, 2, 4);
            buttons.add(firstRow);
            buttons.add(secondRow);
        } else if (articles.size() == 5) {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboardFromArticles(articles, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboardFromArticles(articles, 2, 4);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboardFromArticles(articles, 4, 5);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        } else {
            List<InlineKeyboardButton> firstRow = getListInlineKeyboardFromArticles(articles, 0, 2);
            List<InlineKeyboardButton> secondRow = getListInlineKeyboardFromArticles(articles, 2, 4);
            List<InlineKeyboardButton> thirdRow = getListInlineKeyboardFromArticles(articles, 4, 6);
            buttons.add(firstRow);
            buttons.add(secondRow);
            buttons.add(thirdRow);
        }
    }

    private static List<InlineKeyboardButton> getListInlineKeyboardFromTickets(List<TicketJ> tickets, int start, int end) {
        return IntStream
                .range(start, end)
                .mapToObj(index -> InlineKeyboardButton
                        .builder()
                        .text(String.valueOf(tickets.get(index).getTicketNumber()))
                        .callbackData(StringUtil.serialize(new TicketViewDTO(tickets.get(index).getTicketNumber(), 0)))
                        .build())
                .collect(Collectors.toList());
    }

    private static List<InlineKeyboardButton> getListInlineKeyboardFromArticles(List<Article> articles, int start, int end) {
        return IntStream
                .range(start, end)
                .mapToObj(index -> InlineKeyboardButton
                        .builder()
                        .text(String.valueOf(articles.get(index).getArticleID()))
                        .callbackData(StringUtil.serialize(new ArticleViewDTO(String.valueOf(articles
                                .get(index)
                                .getArticleID()), 0)))
                        .build())
                .collect(Collectors.toList());
    }

    public static void editedInlineKeyboard(Update update, InlineKeyboardMarkup.InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder, VirtaBot bot) throws TelegramApiException {
        bot.execute(EditMessageReplyMarkup
                .builder()
                .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkupBuilder.build())
                .build());
    }
}
