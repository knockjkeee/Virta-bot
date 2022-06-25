package ru.newsystems.webservice.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.MessageUpdateDTO;
import ru.newsystems.basecore.model.dto.ParseDTO;
import ru.newsystems.basecore.model.dto.TicketGetDTO;
import ru.newsystems.basecore.model.dto.TicketSearchDTO;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.model.state.TicketState;
import ru.newsystems.basecore.model.state.UpdateHandlerStage;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;
import ru.newsystems.webservice.service.RestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static ru.newsystems.basecore.utils.NumberUtil.getIdByTicketNumber;
import static ru.newsystems.basecore.utils.TelegramUtil.getMessage;

@Component
public class MessageUpdateHandler implements UpdateHandler {

    private final CommandParser commandParser;
    private final RestService restService;
    private final MessageLocalRepo localRepo;
    private final VirtaBot bot;

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
            handleText(update);
            return true;
        }
        return false;
    }

    private boolean handleText(Update update) throws TelegramApiException {

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
                    return true;
                } else {
                    sendErrorMsg(update, text, ticket.get());
                    return false;
                }
            } else {
                sendExceptionMsg(update, text, "id");
                return false;
            }
        } else {
            switch (MessageState.getState(text)) {
                case SENDCOMMENT -> commentHandler(update);
                case EXIT -> exitHandler(update);
                case DOWLOADFILE -> downloadHandler(update);
                default -> sendExceptionMsg(update, text, "tk");
            }
        }
        //TODO send document
        byte[] decode = Base64.getDecoder().decode("MTIzMTIzMTI=".getBytes(StandardCharsets.UTF_8));
//        bot.execute(SendDocument.builder().chatId(update.getMessage().getChatId().toString())
//                .document(new InputFile(new ByteArrayInputStream(decode), "text.txt")).build());
        return false;
    }

    private void commentHandler(Update update) {

    }

    private void downloadHandler(Update update) {

    }

    private void exitHandler(Update update) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .text("✅ Выполнено")
                .replyToMessageId(update.getMessage().getMessageId())
                .chatId(update.getMessage().getChatId().toString())
                .replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build())
                .build());
        localRepo.remove(update.getMessage().getChatId());
    }

    private void sendTicketTextMsg(Update update, TicketJ ticket) throws TelegramApiException {
        String ticketDateTime = ticket.getCreated().replaceAll("\\s+", "T");
        LocalDateTime parseTicket = LocalDateTime.parse(ticketDateTime);
        String ticketText = "`Результат поиска:`"
                + "\n№ __"
                + ticket.getTicketNumber()
                + "__"
                + "\t"
                + TicketState.getState(ticket.getState()).getLabel()
                + "\t"
                + TicketState.getState(ticket.getLock()).getLabel()
                + "\n_Очередь:_ \t"
                + ticket.getQueue()
                + "\nПриоритет:_ \t"
                + ticket.getPriority()
                + "\n_Дата создания:_ \t"
                + parseTicket.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + "\n_Время создания:_ \t"
                + parseTicket.format(DateTimeFormatter.ofPattern("hh:mm:ss"))
                + "\n_Заголовок:_ \t"
                + ticket.getTitle()
                + "\n_Количество комментариев:_ "
                + ticket.getArticles().size();

        Article article = ticket.getArticles().get(ticket.getArticles().size() - 1);
        String articleDatetime = article.getCreateTime().replaceAll("\\s+", "T");
        LocalDateTime parseArticle = LocalDateTime.parse(articleDatetime);
        int sizeAttach = article.getAttachments() == null ? 0 : article.getAttachments().size();
        String resultText = ticketText
                + "\n\n`Крайний комментарий:`"
                + "\n_Дата создания:_\t"
                + parseArticle.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + "\n_Время создания:_\t"
                + parseArticle.format(DateTimeFormatter.ofPattern("hh:mm:ss"))
                + "\n_Заголовок:_\t"
                + replaceAllBindCharacter(article.getSubject())
                + "\n_От кого:_\t"
                + replaceAllBindCharacter(article.getFrom())
                + "\nКому: \t"
                + replaceAllBindCharacter(article.getTo())
                + "\n*Тело сообщения:*\n"
                + replaceAllBindCharacter(article.getBody())
                + "\nКоличество файлов прикрепленных к комментарию: \t"
                + sizeAttach;

        KeyboardRow rowText = new KeyboardRow();
        KeyboardRow rowRequest = new KeyboardRow();
        rowText.add(MessageState.SENDCOMMENT.getName());
        rowText.add(MessageState.DOWLOADFILE.getName());
        rowRequest.add(MessageState.EXIT.getName());

        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(resultText)
                .parseMode(ParseMode.MARKDOWNV2)
                .replyToMessageId(update.getMessage().getMessageId())
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(rowText)
                        .keyboardRow(rowRequest)
                        .build())
                .build());
        MessageUpdateDTO messageUpdateDTO = new MessageUpdateDTO();
        messageUpdateDTO.setUpdate(update);
        messageUpdateDTO.setState(MessageState.SHOW);
        localRepo.update(update.getMessage().getChatId(), messageUpdateDTO);
    }

    private String replaceAllBindCharacter(String text) {
//        char c = '[';
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
                + "\nВ поиск передано не верное значение: \n["
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

    private void sendErrorMsg(Update update, String text, TicketGetDTO ticket) throws TelegramApiException {
        String resultText = "❗️❗❗ \n<b>ErrorCode</b>: "
                + ticket.getError().getErrorCode()
                + ""
                + "\n<b>ErrorMessage</b>: "
                + ticket.getError().getErrorMessage()
                + ""
                + "\nby text: "
                + text;
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
