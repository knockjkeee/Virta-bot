package ru.newsystems.webservice.utils.Telegram;

import org.jetbrains.annotations.NotNull;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.state.TicketState;

import static ru.newsystems.basecore.utils.StringUtil.getDateTimeFormat;


public class Messages {
    @NotNull
    public static String prepareTextTicket(TicketJ ticket) {
        String formatDateTime = getDateTimeFormat(ticket.getCreated());
        return "<pre>Результат поиска:</pre>"
                + "\n№ <i>"
                + ticket.getTicketNumber()
                + "</i>"
                + "\t\t\t"
                + TicketState.getState(ticket.getState()).getLabel()
                + "\t"
                + TicketState.getState(ticket.getLock()).getLabel()
                + "\n<i>От:</i> \t"
                + formatDateTime
                + "\n<i>Очередь:</i> \t"
                + ticket.getQueue()
                + "\n<i>Приоритет:</i> \t"
                + ticket.getPriority()
                + "\n<i>Заголовок:</i> \t"
                + ticket.getTitle();
    }

    @NotNull
    public static String prepareTextArticle(Article article, int sizeAttach, String text) {
        String formatDateTime = getDateTimeFormat(article.getCreateTime());
        return text
                + "\n<i>От:</i> \t"
                + formatDateTime
                + "\n<i>Заголовок:</i> \t"
                + article.getSubject()
                + "\n<i>От кого:</i> \t"
                + article.getFrom().replaceAll("<", "").replaceAll(">", "")
                + "\n<i>Кому:</i> \t"
                + article.getTo()
                + "\n<i>Тело сообщения:</i> "
                + article.getBody()
                + "\n<i>Количество файлов прикрепленных к комментарию:</i> \t"
                + sizeAttach;
    }
}
