package ru.newsystems.webservice.handler.update;

import org.jetbrains.annotations.NotNull;
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
import ru.newsystems.basecore.model.dto.callback.SendDataDTO;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.basecore.model.dto.domain.TicketSearchDTO;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.model.state.UpdateHandlerStage;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;
import ru.newsystems.basecore.utils.StringUtil;
import ru.newsystems.webservice.handler.messaga.MessageHandler;
import ru.newsystems.webservice.service.RestService;

import java.util.List;
import java.util.Optional;

import static ru.newsystems.basecore.utils.NumberUtil.getIdByTicketNumber;
import static ru.newsystems.webservice.utils.Telegram.Action.getMessage;
import static ru.newsystems.webservice.utils.Telegram.Messages.prepareTextArticle;
import static ru.newsystems.webservice.utils.Telegram.Messages.prepareTextTicket;
import static ru.newsystems.webservice.utils.Telegram.Notification.sendErrorMsg;
import static ru.newsystems.webservice.utils.Telegram.Notification.sendExceptionMsg;

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
        bot.execute(SendChatAction
                .builder()
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
                    updateLocalRepo(update, ticket);
                    return true;
                } else {
                    sendErrorMsg(bot, update, text, ticket.get().getError());
                    return false;
                }
            } else {
                sendExceptionMsg(update, text, "id", bot);
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
            sendExceptionMsg(update, text, "tk", bot);
        }
        return false;
    }

    private void sendTicketTextMsg(Update update, TicketJ ticket) throws TelegramApiException {

        String resultText = prepareText(ticket);

        List<List<InlineKeyboardButton>> buttons = List.of(List.of(InlineKeyboardButton
                .builder()
                .text("Отправить комментарий")
                .callbackData(StringUtil.serialize(new SendDataDTO(ticket.getTicketNumber())))
                .build()), List.of(InlineKeyboardButton
                .builder()
                .text("Выгрузить документы")
                .callbackData(StringUtil.serialize(new DownloadFilesDTO(update.getMessage().getChatId(), ticket.getTicketNumber(), 0L, "s")))
                .build()));

        bot.execute(SendMessage
                .builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(resultText)
                .parseMode(ParseMode.HTML)
                .protectContent(true)
                .replyToMessageId(update.getMessage().getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

    @NotNull
    private String prepareText(TicketJ ticket) {
        String ticketText = prepareTextTicket(ticket);
        String countTMsgText = ticketText + "\n<i>Количество комментариев:</i> " + ticket.getArticles().size();
        Article lastArticle = ticket.getArticles().get(ticket.getArticles().size() - 1);
        int sizeAttach = lastArticle.getAttachments() == null ? 0 : lastArticle.getAttachments().size();
        String lastComment = countTMsgText + "\n\n<pre>Последний комментарий:</pre>";
        return prepareTextArticle(lastArticle, sizeAttach, lastComment);
    }

    private void updateLocalRepo(Update update, Optional<TicketGetDTO> ticket) {
        MessageGetDTO messageGetDTO = new MessageGetDTO();
        messageGetDTO.setTicket(ticket.get().getTickets().get(0));
        messageGetDTO.setState(MessageState.SHOW);
        localRepo.update(update.getMessage().getChatId(), messageGetDTO);
    }


    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.MESSAGE;
    }

}
