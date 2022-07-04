package ru.newsystems.webservice.handler.messaga;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.webservice.task.SendOperationTask;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.Attachment;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;
import ru.newsystems.basecore.model.dto.domain.TicketUpdateDTO;
import ru.newsystems.basecore.model.state.ContentTypeState;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;
import ru.newsystems.webservice.service.RestService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static ru.newsystems.webservice.utils.TelegramUtil.closeReplyKeyBoard;
import static ru.newsystems.webservice.utils.TelegramUtil.sendErrorMsg;

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

                    SendNewComment(update, req);
                    return true;
                }

                if (update.getMessage().hasPhoto()) {
                    List<String> messages = splitMessageText(update.getMessage().getCaption(), "#");
                    if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;
                    RequestUpdateDTO req = prepareReqWithPhoto(update, replyTexts, messages);
                    SendNewComment(update, req);
                    return true;
                }

                if (update.getMessage().hasDocument()) {
                    List<String> messages = splitMessageText(update.getMessage().getCaption(), "#");
                    if (checkCorrectlySendFormatSubjectMessage(update, messages)) return true;
                    RequestUpdateDTO req = prepareReqWithDocument(update, replyTexts, messages);
                    SendNewComment(update, req);
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

    private void SendNewComment(Update update, RequestUpdateDTO req) throws TelegramApiException {
        Optional<TicketUpdateDTO> ticketOperationUpdate = restService.getTicketOperationUpdate(req);
        if (ticketOperationUpdate.isPresent() && ticketOperationUpdate.get().getError() == null) {
            closeReplyKeyBoard(update, bot, true);
        } else {
            sendErrorMsg(bot, update, update.getMessage().getReplyToMessage().getText(), ticketOperationUpdate.get()
                    .getError());
        }
    }

    private Attachment prepareAttachmentFromDocument(Update update) throws TelegramApiException {
        Document document = update.getMessage().getDocument();
        String base64 = prepareBase64(document.getFileId(), false);
        return prepareAttach(base64, document.getMimeType(), document.getFileName());
    }

    private Attachment prepareAttachmentFromPhoto(Update update) throws TelegramApiException {
        String filePath = getFilePath(update);
        String base64 = getBase64(filePath);
        String fileName = filePath.split("/")[1];
        String contentType = ContentTypeState.getState(fileName.split("\\.")[1]).getContent();
        return prepareAttach(base64, contentType, fileName);
    }

    @NotNull
    private RequestUpdateDTO prepareReqWithDocument(Update update, List<String> texts, List<String> messages) throws TelegramApiException {
        Document document = update.getMessage().getDocument();
        String base64 = prepareBase64(document.getFileId(), false);
        return prepareReqWithAttachment(texts, messages, base64, document.getMimeType(), document.getFileName());
    }

    @NotNull
    private RequestUpdateDTO prepareReqWithPhoto(Update update, List<String> texts, List<String> messages) throws TelegramApiException {
        String filePath = getFilePath(update);
        String base64 = getBase64(filePath);
        String fileName = filePath.split("/")[1];
        String contentType  = ContentTypeState.getState(fileName.split("\\.")[1]).getContent();
        return prepareReqWithAttachment(texts, messages, base64, contentType, fileName);
    }

    @NotNull
    private RequestUpdateDTO prepareReqWithAttachment(List<String> texts, List<String> messages, String base64, String contentType, String fileName) {
        RequestUpdateDTO req = prepareReqWithMessage(texts, messages);
        Attachment attach = prepareAttach(base64, contentType, fileName);
        req.setAttaches(List.of(attach));
        return req;
    }

    @NotNull
    private RequestUpdateDTO prepareReqWithMessage(List<String> texts, List<String> messages) {
        RequestUpdateDTO req = new RequestUpdateDTO();
        req.setTicketNumber(Long.valueOf(texts.get(1)));
        Article article = new Article();
        article.setSubject(messages.get(0));
        article.setBody(messages.get(1));
        req.setArticle(article);
        return req;
    }

    @NotNull
    private Attachment prepareAttach(String base64, String contentType, String fileName) {
        Attachment attach = new Attachment();
        attach.setContent(base64);
        attach.setContentType(contentType);
        attach.setFilename(fileName);
        return attach;
    }

    private String getFilePath(Update update) throws TelegramApiException {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize photo = photos.size() == 2 ? photos.get(1) : photos.get(0);
        return prepareBase64(photo.getFileId(), true);
    }

    @NotNull
    private List<String> splitMessageText(String caption, String s) {
        return Arrays.stream(caption.split(s)).map(String::strip).collect(Collectors.toList());
    }

    private String prepareBase64(String fileId, boolean isPhoto) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        String filePath = bot.execute(getFile).getFilePath();
        return isPhoto ? filePath : getBase64(filePath);
    }

    private @NotNull String getBase64(String filePath) throws TelegramApiException {
        File file = bot.downloadFile(filePath);
        try {
            byte[] encode = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
            return new String(encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}

