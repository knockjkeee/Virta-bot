package ru.newsystems.webservice.handler.messaga;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Article;
import ru.newsystems.basecore.model.domain.Attachment;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;
import ru.newsystems.basecore.model.dto.domain.TicketUpdateDTO;
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
import java.util.stream.Collectors;

@Component
public class MessageSendCommentHandler implements MessageHandler {

    private final MessageLocalRepo localRepo;
    private final RestService restService;
    private final VirtaBot bot;

    public MessageSendCommentHandler(MessageLocalRepo localRepo, RestService restService, VirtaBot bot) {
        this.localRepo = localRepo;
        this.restService = restService;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        if (update.getMessage().getReplyToMessage() != null) {
            List<String> splitText = Arrays.stream(update.getMessage().getReplyToMessage().getText().split("№"))
                    .map(String::strip)
                    .collect(Collectors.toList());
            if (splitText.get(0).contains(MessageState.SENDCOMMENT.getName())) {
                //TODO code
                // 1 message
                // 2 photo
                // 3 document



                if (update.getMessage().hasText()) {

                    List<String> splitMessage = Arrays.stream(update.getMessage().getText().split("#"))
                            .map(String::strip)
                            .collect(Collectors.toList());

                    RequestUpdateDTO req = new RequestUpdateDTO();
                    req.setTicketNumber(Long.valueOf(splitText.get(1)));
                    Article article = new Article();
                    article.setSubject(splitMessage.get(0));
                    article.setBody(splitMessage.get(1));
                    req.setArticle(article);

                    Optional<TicketUpdateDTO> ticketOperationUpdate = restService.getTicketOperationUpdate(req);
                    System.out.println("Message");

                    return true;
                }


                if (update.getMessage().hasPhoto()) {
                    List<String> splitMessage = Arrays.stream(update.getMessage().getCaption().split("#"))
                            .map(String::strip)
                            .collect(Collectors.toList());

                    List<PhotoSize> photos = update.getMessage().getPhoto();
//                    PhotoSize photo = photos.size() == 2 ? photos.get(1) : photos.get(0);
                    PhotoSize photo = photos.get(0);
                    String filePath = prepareBase64(photo.getFileId(), true);
                    String base64 = getBase64(filePath);
                    String fileName = filePath.split("/")[1];
                    String contentType = fileName.contains(".png") ? "image/x-png" : "image/jpeg";

                    RequestUpdateDTO req = new RequestUpdateDTO();
                    req.setTicketNumber(Long.valueOf(splitText.get(1)));
                    Article article = new Article();
                    article.setSubject(splitMessage.get(0));
                    article.setBody(splitMessage.get(1));
                    req.setArticle(article);
                    Attachment attach = new Attachment();
                    attach.setContent(base64);
                    attach.setContentType(contentType);
                    attach.setFilename(fileName);
                    req.setAttaches(List.of(attach));
                    Optional<TicketUpdateDTO> ticketOperationUpdate = restService.getTicketOperationUpdate(req);
                    System.out.println("Photo");


                    return true;

                }

                if (update.getMessage().hasDocument()) {
                    List<String> splitMessage = Arrays.stream(update.getMessage().getCaption().split("#"))
                            .map(String::strip)
                            .collect(Collectors.toList());
                    Document document = update.getMessage().getDocument();
                    String base64 = prepareBase64(document.getFileId(), false);
                    System.out.println("Document");

                    RequestUpdateDTO req = new RequestUpdateDTO();
                    req.setTicketNumber(Long.valueOf(splitText.get(1)));
                    Article article = new Article();
                    article.setSubject(splitMessage.get(0));
                    article.setBody(splitMessage.get(1));
                    req.setArticle(article);
                    Attachment attach = new Attachment();
                    attach.setContent(base64);
                    attach.setContentType(document.getMimeType());
                    attach.setFilename(document.getFileName());
                    req.setAttaches(List.of(attach));
                    Optional<TicketUpdateDTO> ticketOperationUpdate = restService.getTicketOperationUpdate(req);
                    System.out.println("Document");




                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
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

