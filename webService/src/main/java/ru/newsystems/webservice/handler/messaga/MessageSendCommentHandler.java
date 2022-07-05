package ru.newsystems.webservice.handler.messaga;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Attachment;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.webservice.service.RestService;
import ru.newsystems.webservice.task.SendLocalRepo;
import ru.newsystems.webservice.task.SendOperationTask;
import ru.newsystems.webservice.task.SendUpdateDTO;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static ru.newsystems.webservice.utils.TelegramUtil.*;

@Component
public class MessageSendCommentHandler implements MessageHandler {

    public static final int DELAY_FOR_ADD_DOCS_OR_PIC = 5;
    public static final int DELAY_FOR_AFTER_ADD_MSG = 3;
    private final SendLocalRepo localRepo;
    private final ScheduledExecutorService executor;
    private final RestService restService;
    private final VirtaBot bot;

    public MessageSendCommentHandler(SendLocalRepo localRepo, ScheduledExecutorService executor, RestService restService, VirtaBot bot) {
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
                if (update.getMessage().hasText()) {
                    List<String> messages = splitMessageText(update.getMessage().getText(), "#");
                    if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;
                    RequestUpdateDTO req = prepareReqWithMessage(replyTexts, messages);
                    prepareTaskForExecutor(update, req);
                    return true;
                }

                if (update.getMessage().hasPhoto()) {
                    SendUpdateDTO sendUpdateDTO = localRepo.get(update.getMessage().getChatId());
                    if (sendUpdateDTO != null && !sendUpdateDTO.getSchedule().isDone()) {
                        sendUpdateDTO.stopSchedule();
                        SendOperationTask task = sendUpdateDTO.getTask();
                        Attachment attachment = prepareAttachmentFromPhoto(update, bot);
                        prepareTaskForExecutor(update, sendUpdateDTO, task, attachment);
                        return true;
                    } else {
                        List<String> messages = splitMessageText(update.getMessage().getCaption(), "#");
                        if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;
                        RequestUpdateDTO req = prepareReqWithPhoto(update, replyTexts, messages, bot);
                        if ((sendUpdateDTO == null || sendUpdateDTO.getSchedule().isDone())
                                && update.getMessage().getMediaGroupId() != null) {
                            prepareTaskForExecutor(update, req);
                            return true;
                        }
                        sendNewComment(update, req, restService, bot);
                        return true;
                    }
                }

                if (update.getMessage().hasDocument()) {
                    SendUpdateDTO sendUpdateDTO = localRepo.get(update.getMessage().getChatId());
                    if (sendUpdateDTO != null && !sendUpdateDTO.getSchedule().isDone()) {
                        sendUpdateDTO.stopSchedule();
                        SendOperationTask task = sendUpdateDTO.getTask();
                        Attachment attachment = prepareAttachmentFromDocument(update, bot);
                        prepareTaskForExecutor(update, sendUpdateDTO, task, attachment);
                        return true;
                    }

                    List<String> messages = splitMessageText(update.getMessage().getCaption(), "#");
                    if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;
                    RequestUpdateDTO req = prepareReqWithDocument(update, replyTexts, messages, bot);
                    sendNewComment(update, req, restService, bot);
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private void prepareTaskForExecutor(Update update, SendUpdateDTO sendUpdateDTO, SendOperationTask task, Attachment attachment) {
        task.updateAttachment(attachment);
        ScheduledFuture<?> schedule = executor.schedule(task, DELAY_FOR_AFTER_ADD_MSG, TimeUnit.SECONDS);
        sendUpdateDTO.setSchedule(schedule);
        sendUpdateDTO.setTask(task);
        localRepo.update(update.getMessage().getDate().longValue() - 1, sendUpdateDTO);
    }

    private void prepareTaskForExecutor(Update update, RequestUpdateDTO req) {
        SendOperationTask task = SendOperationTask.builder()
                .req(req)
                .update(update)
                .bot(bot)
                .restService(restService)
                .build();
        ScheduledFuture<?> schedule = executor.schedule(task, DELAY_FOR_ADD_DOCS_OR_PIC, TimeUnit.SECONDS);
        SendUpdateDTO sendUpdateDTO = SendUpdateDTO.builder().task(task).schedule(schedule).build();
        localRepo.update(update.getMessage().getChatId(), sendUpdateDTO);
    }

    private boolean checkCorrectlySendFormatSubjectMessage(Update update, List<String> splitMessage) throws TelegramApiException {
        if (splitMessage.size() != 2) {
            closeReplyKeyBoard(update, bot, false);
            return true;
        }
        return false;
    }
}

