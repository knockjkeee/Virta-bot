package ru.newsystems.webservice.handler.messaga;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Attachment;
import ru.newsystems.basecore.model.dto.domain.RequestDataDTO;
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
public class MessageSendDataHandler implements MessageHandler {

    public static final int DELAY_FOR_ADD_DOCS_OR_PIC = 5;
    public static final int DELAY_AFTER_ADD_MSG = 3;
    private final SendLocalRepo localRepo;
    private final ScheduledExecutorService executor;
    private final RestService restService;
    private final VirtaBot bot;

    public MessageSendDataHandler(SendLocalRepo localRepo, ScheduledExecutorService executor, RestService restService, VirtaBot bot) {
        this.localRepo = localRepo;
        this.executor = executor;
        this.restService = restService;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        if (update.getMessage().getReplyToMessage() != null) {
            List<String> replyTexts = splitMessageText(update.getMessage().getReplyToMessage().getText(), "№");
            boolean isSendComment = replyTexts.get(0).contains(MessageState.SENDCOMMENT.getName());
            boolean isCreateTicket = replyTexts.get(0).contains(MessageState.CREATETICKET.getName());
            if (isSendComment || isCreateTicket) {
                if (update.getMessage().hasText()) {
                    RequestDataDTO req = prepareReqWithMessage(replyTexts, update.getMessage().getText());
                    prepareTaskForExecutor(update, req, isSendComment);
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
                        RequestDataDTO req = prepareReqWithPhoto(update, replyTexts, update.getMessage().getCaption(), bot);
                        if ((sendUpdateDTO == null || sendUpdateDTO.getSchedule().isDone())
                                && update.getMessage().getMediaGroupId() != null) {
                            prepareTaskForExecutor(update, req, isSendComment);
                            return true;
                        }
                        if (isSendComment) {
                            sendNewComment(update, req, restService, bot);
                        }else{
                            sendCreateTicket(update, req, restService, bot);
                        }
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
                    RequestDataDTO req = prepareReqWithDocument(update, replyTexts, update.getMessage().getCaption(), bot);
                    if (isSendComment) {
                        sendNewComment(update, req, restService, bot);
                    }else{
                        sendCreateTicket(update, req, restService, bot);
                    }
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
        ScheduledFuture<?> schedule = executor.schedule(task, DELAY_AFTER_ADD_MSG, TimeUnit.SECONDS);
        sendUpdateDTO.setSchedule(schedule);
        sendUpdateDTO.setTask(task);
        localRepo.update(update.getMessage().getDate().longValue() - 1, sendUpdateDTO);
    }

    private void prepareTaskForExecutor(Update update, RequestDataDTO req, boolean isSendComment) {
        SendOperationTask task = SendOperationTask.builder()
                .req(req)
                .update(update)
                .bot(bot)
                .restService(restService)
                .isSendComment(isSendComment)
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

