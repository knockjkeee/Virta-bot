package ru.newsystems.webservice.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
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
import ru.newsystems.basecore.model.dto.domain.RequestDataDTO;
import ru.newsystems.basecore.model.dto.domain.TicketUpdateCreateDTO;
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

    public static void resultOperationToChat(Update update, VirtaBot bot, boolean isSuccess) throws TelegramApiException {
        String text = isSuccess ? "<pre>✅ Выполнено</pre>" : "<pre>⛔️ Ошибка в запросе</pre>";
        if (update.hasCallbackQuery()) {
            bot.execute(SendMessage
                    .builder()
                    .text(text)
                    .replyToMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                    .parseMode(ParseMode.HTML)
                    .build());
        } else {
            bot.execute(SendMessage
                    .builder()
                    .text(text)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .chatId(update.getMessage().getChatId().toString())
                    .parseMode(ParseMode.HTML)
                    //.replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build())
                    .build());
        }
    }

    public static void resultOperationToChat(Message message, VirtaBot bot, boolean isSuccess) throws TelegramApiException {
        String text = isSuccess ? "<pre>✅ Выполнено</pre>" : "<pre>❎ Открытых заявок нет</pre>";
        bot.execute(SendMessage
                .builder()
                .text(text)
                .replyToMessageId(message.getMessageId())
                .chatId(message.getChatId().toString())
                .parseMode(ParseMode.HTML)
                .build());
    }

    public static void receiveReqNum(Update update, VirtaBot bot, Long reqNum) throws TelegramApiException {
        bot.execute(SendMessage
                .builder()
                .text("<pre>Номер созданной заявки: " + reqNum + "</pre>")
                .replyToMessageId(update.getMessage().getMessageId())
                .chatId(update.getMessage().getChatId().toString())
                .parseMode(ParseMode.HTML)
                .build());
    }

    public static void sendErrorMsg(VirtaBot bot, Update update, String text, Error error) throws TelegramApiException {
        String resultText = "❗️❗❗ \n<pre>ErrorCode</pre>: "
                + error.getErrorCode()
                + ""
                + "\n<pre>ErrorMessage</pre>: "
                + error.getErrorMessage()
                + ""
                + "\nby text: "
                + text;
        if (update.hasCallbackQuery()) {
            bot.execute(SendMessage
                    .builder()
                    .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                    .text(resultText)
                    .parseMode(ParseMode.HTML)
                    .replyToMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .build());
        } else {
            bot.execute(SendMessage
                    .builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text(resultText)
                    .parseMode(ParseMode.HTML)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build());
        }
    }

    public static void sendNewComment(Update update, RequestDataDTO req, RestService restService, VirtaBot bot) throws TelegramApiException {
        Optional<TicketUpdateCreateDTO> ticketOperationUpdate = restService.getTicketOperationUpdate(req);
        if (ticketOperationUpdate.isPresent() && ticketOperationUpdate.get().getError() == null) {
            resultOperationToChat(update, bot, true);
        } else {
            sendErrorMsg(bot, update, update.getMessage().getReplyToMessage().getText(), ticketOperationUpdate
                    .get()
                    .getError());
        }
    }

    public static void sendCreateTicket(Update update, RequestDataDTO req, RestService restService, VirtaBot bot) throws TelegramApiException {
        Optional<TicketUpdateCreateDTO> ticketOperationUpdate = restService.getTicketOperationCreate(req);
        if (ticketOperationUpdate.isPresent() && ticketOperationUpdate.get().getError() == null) {
            resultOperationToChat(update, bot, true);
            receiveReqNum(update, bot, ticketOperationUpdate.get().getTicketNumber());
        } else {
            sendErrorMsg(bot, update, update.getMessage().getReplyToMessage().getText(), ticketOperationUpdate
                    .get()
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
    public static RequestDataDTO prepareReqWithMessage(List<String> replyTexts, String body) {
        RequestDataDTO req = new RequestDataDTO();
        req.setTicketNumber(replyTexts.size() > 1 ? Long.parseLong(replyTexts.get(1)) : 0);
        Article article = new Article();
        article.setBody(body);
        req.setArticle(article);
        return req;
    }

    @NotNull
    public static RequestDataDTO prepareReqWithPhoto(Update update, List<String> replyTexts, String body, VirtaBot bot) throws TelegramApiException {
        String filePath = getFilePath(update, bot);
        String base64 = getBase64(filePath, bot);
        String fileName = filePath.split("/")[1];
        String contentType = ContentTypeState.getState(fileName.split("\\.")[1]).getContent();
        return prepareReqWithAttachment(replyTexts, body, base64, contentType, fileName);
    }

    @NotNull
    public static RequestDataDTO prepareReqWithDocument(Update update, List<String> replyTexts, String body, VirtaBot bot) throws TelegramApiException {
        Document document = update.getMessage().getDocument();
        String base64 = prepareBase64(document.getFileId(), false, bot);
        return prepareReqWithAttachment(replyTexts, body, base64, document.getMimeType(), document.getFileName());
    }

    @NotNull
    private static RequestDataDTO prepareReqWithAttachment(List<String> replyTexts, String body, String base64, String contentType, String fileName) {
        RequestDataDTO req = prepareReqWithMessage(replyTexts, body);
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
