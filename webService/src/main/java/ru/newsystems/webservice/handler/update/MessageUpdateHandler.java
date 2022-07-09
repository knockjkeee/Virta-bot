package ru.newsystems.webservice.handler.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.MessageGetDTO;
import ru.newsystems.basecore.model.dto.ParseDTO;
import ru.newsystems.basecore.model.dto.callback.DownloadFilesDTO;
import ru.newsystems.basecore.model.dto.callback.SendCommentDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.dto.domain.TicketSearchDTO;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.model.state.TicketState;
import ru.newsystems.basecore.model.state.UpdateHandlerStage;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;
import ru.newsystems.basecore.utils.StringUtil;
import ru.newsystems.webservice.handler.messaga.MessageHandler;
import ru.newsystems.webservice.service.RestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.newsystems.basecore.utils.NumberUtil.getIdByTicketNumber;
import static ru.newsystems.webservice.utils.TelegramUtil.getMessage;
import static ru.newsystems.webservice.utils.TelegramUtil.sendErrorMsg;

@Component
public class MessageUpdateHandler implements UpdateHandler {

    private final CommandParser commandParser;
    private final RestService restService;
    private final MessageLocalRepo localRepo;
    private final VirtaBot bot;

    @Autowired
    private List<MessageHandler> messageHandlers;

    public MessageUpdateHandler(CommandParser commandParser, RestService restService, MessageLocalRepo localRepo, VirtaBot bot) {
        this.commandParser = commandParser;
        this.localRepo = localRepo;
        this.restService = restService;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        Message message = getMessage(update);
        if (message == null) return false;
        String text = message.getText();
        Optional<ParseDTO> command = commandParser.parseCommand(text);
        if (command.isEmpty()) {
            return handleText(update);
        }
        return false;
    }

    private boolean handleText(Update update) throws TelegramApiException {
        bot.execute(SendChatAction.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .action(ActionType.TYPING.toString())
                .build());
        String text = update.getMessage().getText();
        long tk = getIdByTicketNumber(text);
        Optional<TicketSearchDTO> ticketSearch = tk
                != 0 ? restService.getTicketOperationSearch(List.of(tk)) : Optional.empty();
        if (ticketSearch.isPresent()) {
            List<Long> ticketsId = ticketSearch.get().getTicketIDs();
            Optional<TicketGetDTO> ticket = restService.getTicketOperationGet(ticketsId);
            if (ticket.isPresent()) {
                if (ticket.get().getError() == null) {
                    sendTicketTextMsg(update, ticket.get().getTickets().get(0));
                    MessageGetDTO messageGetDTO = new MessageGetDTO();
                    messageGetDTO.setTicket(ticket.get().getTickets().get(0));
                    messageGetDTO.setState(MessageState.SHOW);
                    localRepo.update(update.getMessage().getChatId(), messageGetDTO);
                    return true;
                } else {
                    sendErrorMsg(bot, update, text, ticket.get().getError());
                    return false;
                }
            } else {
                sendExceptionMsg(update, text, "id");
                return false;
            }
        } else {
            for (MessageHandler messageHandler : messageHandlers) {
                try {
                    if (messageHandler.handleUpdate(update)) {
                        //TODO log good work
                        return true;
                    }
                    //TODO log bad work, search entity
                } catch (Exception e) {
                    //TODO log excpt work
                    e.printStackTrace();
                }
            }
            if (update.getMessage().hasPhoto()) {
                return false;
            }
            sendExceptionMsg(update, text, "tk");
        }
        return false;
    }

    private void sendTicketTextMsg(Update update, TicketJ ticket) throws TelegramApiException {
        String ticketDateTime = ticket.getCreated().replaceAll("\\s+", "T");
        LocalDateTime parseTicket = LocalDateTime.parse(ticketDateTime);
        String ticketText = "*Результат поиска:*"
                + "\n№ __"
                + ticket.getTicketNumber()
                + "__"
                + "\t\t\t"
                + TicketState.getState(ticket.getState()).getLabel()
                + "\t"
                + TicketState.getState(ticket.getLock()).getLabel()
                + "\n_От:_ \t"
                + parseTicket.format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss"))
                + "\n_Очередь:_ \t"
                + ticket.getQueue()
                + "\n_Приоритет:_ \t"
                + ticket.getPriority()
                + "\n_Заголовок:_ \t"
                + ticket.getTitle()
                + "\n_Количество комментариев:_ "
                + ticket.getArticles().size();

        Article article = ticket.getArticles().get(ticket.getArticles().size() - 1);
        String articleDatetime = article.getCreateTime().replaceAll("\\s+", "T");
        LocalDateTime parseArticle = LocalDateTime.parse(articleDatetime);
        int sizeAttach = article.getAttachments() == null ? 0 : article.getAttachments().size();
        String resultText = ticketText
                + "\n\n*Последний комментарий:*"
                + "\n_От:_ \t"
                + parseArticle.format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss"))
                + "\n_Заголовок:_\t"
                + replaceAllBindCharacter(article.getSubject())
                + "\n_От кого:_\t"
                + replaceAllBindCharacter(article.getFrom())
                + "\nКому: \t"
                + replaceAllBindCharacter(article.getTo())
                + "\n_Тело сообщения:_ "
                + replaceAllBindCharacter(article.getBody())
                + "\n_Количество файлов прикрепленных к комментарию:_ \t"
                + sizeAttach;

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        buttons.add(List.of(InlineKeyboardButton.builder()
                .text("Отправить комментарий")
                .callbackData(StringUtil.serialize(new SendCommentDTO("Отправить комментарий", true)))
                .build()));
        buttons.add(List.of(InlineKeyboardButton.builder()
                .text("Выгрузить документы")
                .callbackData(StringUtil.serialize(new DownloadFilesDTO("Выгрузить документы", false)))
                .build()));

        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(resultText)
                .parseMode(ParseMode.MARKDOWNV2)
                .replyToMessageId(update.getMessage().getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

    private String replaceAllBindCharacter(String text) {
        return text.replaceAll("#", "/")
                .replaceAll("]", "/")
                .replace('[', '/')
                .replaceAll("-", "/")
                .replaceAll("\\.", "")
                .replaceAll(">", "/")
                .replace("(", "/")
                .replace(")", "/");
    }

    private void sendExceptionMsg(Update update, String text, String service) throws TelegramApiException {
        String resultText = "❗❗❗️ \n<b>Ошибка в запросе</b>"
                + "\nВ поиск передано не верное значение: ["
                + service
                + "] <b>"
                + text
                + "</b>\nПовторите запрос с корректным id";
        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(resultText)
                .parseMode("html")
                .replyToMessageId(update.getMessage().getMessageId())
                .build());
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.MESSAGE;
    }

}
