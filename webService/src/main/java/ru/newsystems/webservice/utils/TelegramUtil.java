package ru.newsystems.webservice.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.Attachment;
import ru.newsystems.basecore.model.domain.Error;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;
import ru.newsystems.basecore.model.dto.domain.TicketUpdateDTO;
import ru.newsystems.basecore.model.state.ContentTypeState;
import ru.newsystems.webservice.service.RestService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TelegramUtil {

    @Nullable
    public static Message getMessage(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        Message message = update.getMessage();
        if (!message.hasText() && !message.hasPhoto()) {
            if (!message.hasDocument()) {
                return null;
            }
        }
        return message;
    }

    public static void closeReplyKeyBoard(Update update, VirtaBot bot, boolean isSuccess) throws TelegramApiException {
        String text = isSuccess ? "✅ Выполнено" : "⛔️ В формате \"Заголовок/Сообщение\" ошибка, проверьте рекомендации";
        if (update.hasCallbackQuery()) {
            bot.execute(SendMessage.builder()
                    .text(text)
                    .replyToMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                    .build());
        } else {
            bot.execute(SendMessage.builder()
                    .text(text)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .chatId(update.getMessage().getChatId().toString())
                    //.replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build())
                    .build());
        }
    }

    public static void sendErrorMsg(VirtaBot bot, Update update, String text, Error error) throws TelegramApiException {
        String resultText = "❗️❗❗ \n<b>ErrorCode</b>: "
                + error.getErrorCode()
                + ""
                + "\n<b>ErrorMessage</b>: "
                + error.getErrorMessage()
                + ""
                + "\nby text: "
                + text;
        if (update.hasCallbackQuery()) {
            bot.execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                    .text(resultText)
                    .parseMode("html")
                    .replyToMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .build());
        } else {
            bot.execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text(resultText)
                    .parseMode("html")
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build());
        }
    }

    public static void sendNewComment(Update update, RequestUpdateDTO req, RestService restService, VirtaBot bot) throws TelegramApiException {
        Optional<TicketUpdateDTO> ticketOperationUpdate = restService.getTicketOperationUpdate(req);
        if (ticketOperationUpdate.isPresent() && ticketOperationUpdate.get().getError() == null) {
            closeReplyKeyBoard(update, bot, true);
        } else {
            sendErrorMsg(bot, update, update.getMessage().getReplyToMessage().getText(), ticketOperationUpdate.get()
                    .getError());
        }
    }


    public static Attachment prepareAttachmentFromDocument(Update update, VirtaBot bot) throws TelegramApiException {
        Document document = update.getMessage().getDocument();
        String base64 = prepareBase64(document.getFileId(), false, bot);
        return prepareAttach(base64, document.getMimeType(), document.getFileName());
    }

    public static Attachment prepareAttachmentFromPhoto(Update update, VirtaBot bot) throws TelegramApiException {
        String filePath = getFilePath(update, bot);
        String base64 = getBase64(filePath, bot);
        String fileName = filePath.split("/")[1];
        String contentType = ContentTypeState.getState(fileName.split("\\.")[1]).getContent();
        return prepareAttach(base64, contentType, fileName);
    }

    @NotNull
    public static RequestUpdateDTO prepareReqWithMessage(List<String> texts, List<String> messages) {
        RequestUpdateDTO req = new RequestUpdateDTO();
        req.setTicketNumber(Long.valueOf(texts.get(1)));
        Article article = new Article();
        article.setSubject(messages.get(0));
        article.setBody(messages.get(1));
        req.setArticle(article);
        return req;
    }

    @NotNull
    public static RequestUpdateDTO prepareReqWithPhoto(Update update, List<String> texts, List<String> messages, VirtaBot bot) throws TelegramApiException {
        String filePath = getFilePath(update, bot);
        String base64 = getBase64(filePath, bot);
        String fileName = filePath.split("/")[1];
        String contentType  = ContentTypeState.getState(fileName.split("\\.")[1]).getContent();
        return prepareReqWithAttachment(texts, messages, base64, contentType, fileName);
    }

    @NotNull
    public static RequestUpdateDTO prepareReqWithDocument(Update update, List<String> texts, List<String> messages, VirtaBot bot) throws TelegramApiException {
        Document document = update.getMessage().getDocument();
        String base64 = prepareBase64(document.getFileId(), false, bot);
        return prepareReqWithAttachment(texts, messages, base64, document.getMimeType(), document.getFileName());
    }

    @NotNull
    private static RequestUpdateDTO prepareReqWithAttachment(List<String> texts, List<String> messages, String base64, String contentType, String fileName) {
        RequestUpdateDTO req = prepareReqWithMessage(texts, messages);
        Attachment attach = prepareAttach(base64, contentType, fileName);
        req.setAttaches(List.of(attach));
        return req;
    }


    @NotNull
    private static Attachment prepareAttach(String base64, String contentType, String fileName) {
        Attachment attach = new Attachment();
        attach.setContent(base64);
        attach.setContentType(contentType);
        attach.setFilename(fileName);
        return attach;
    }

    private static String getFilePath(Update update, VirtaBot bot) throws TelegramApiException {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize photo = photos.size() == 2 ? photos.get(1) : photos.get(0);
        return prepareBase64(photo.getFileId(), true, bot);
    }

    @NotNull
    public static List<String> splitMessageText(String caption, String s) {
        return Arrays.stream(caption.split(s)).map(String::strip).collect(Collectors.toList());
    }

    private static String prepareBase64(String fileId, boolean isPhoto, VirtaBot bot) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        String filePath = bot.execute(getFile).getFilePath();
        return isPhoto ? filePath : getBase64(filePath, bot);
    }

    private static @NotNull String getBase64(String filePath, VirtaBot bot) throws TelegramApiException {
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
