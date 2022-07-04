package ru.newsystems.webservice.handler.messaga;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;
import ru.newsystems.webservice.service.RestService;
import ru.newsystems.webservice.task.SendOperationTask;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static ru.newsystems.webservice.utils.TelegramUtil.*;

@Component
public class MessageSendCommentHandler implements MessageHandler {

    private final MessageLocalRepo localRepo;
    private final ScheduledExecutorService executor;
    private final RestService restService;
    private final VirtaBot bot;

    public MessageSendCommentHandler(MessageLocalRepo localRepo, ScheduledExecutorService executor, RestService restService, VirtaBot bot) {
        this.localRepo = localRepo;
        this.executor = executor;
        this.restService = restService;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        if (update.getMessage().getReplyToMessage() != null) {
            List<String> replyTexts = splitMessageText(update.getMessage().getReplyToMessage().getText(), "№");
            if (replyTexts.get(0).contains(MessageState.SENDCOMMENT.getName())) {
                //TODO code
                // 1 message
                // 2 photo
                // 3 document

                if (update.getMessage().hasText()) {
                    List<String> messages = splitMessageText(update.getMessage().getText(), "#");
                    if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;

                    RequestUpdateDTO req = prepareReqWithMessage(replyTexts, messages);
                    SendOperationTask task = new SendOperationTask();

                    SendNewComment(update, req, restService, bot);
                    return true;
                }

                if (update.getMessage().hasPhoto()) {
                    List<String> messages = splitMessageText(update.getMessage().getCaption(), "#");
                    if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;
                    RequestUpdateDTO req = prepareReqWithPhoto(update, replyTexts, messages, bot);
                    SendNewComment(update, req, restService, bot);
                    return true;
                }

                if (update.getMessage().hasDocument()) {
                    List<String> messages = splitMessageText(update.getMessage().getCaption(), "#");
                    if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;
                    RequestUpdateDTO req = prepareReqWithDocument(update, replyTexts, messages, bot);
                    SendNewComment(update, req, restService, bot);
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private boolean checkCorrectlySendFormatSubjectMessage(Update update, List<String> splitMessage) throws TelegramApiException {
        if (splitMessage.size() != 2) {
            closeReplyKeyBoard(update, bot, false);
            return true;
        }
        return false;
    }

//    private void SendNewComment(Update update, RequestUpdateDTO req) throws TelegramApiException {
//        Optional<TicketUpdateDTO> ticketOperationUpdate = restService.getTicketOperationUpdate(req);
//        if (ticketOperationUpdate.isPresent() && ticketOperationUpdate.get().getError() == null) {
//            closeReplyKeyBoard(update, bot, true);
//        } else {
//            sendErrorMsg(bot, update, update.getMessage().getReplyToMessage().getText(), ticketOperationUpdate.get()
//                    .getError());
//        }
//    }



}

