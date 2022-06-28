package ru.newsystems.webservice.handler.messaga;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageSendCommentHandler implements MessageHandler {

    private final MessageLocalRepo localRepo;
    private final VirtaBot bot;

    public MessageSendCommentHandler(MessageLocalRepo localRepo, VirtaBot bot) {
        this.localRepo = localRepo;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        if (update.getMessage().getReplyToMessage() != null) {
            List<String> split = Arrays.stream(update.getMessage().getReplyToMessage().getText().split("№"))
                    .map(String::strip)
                    .collect(Collectors.toList());
            if (split.get(0).contains(MessageState.SENDCOMMENT.getName())) {
                //TODO code
                // 1 message
                // 2 photo
                // 3 document

                if (update.getMessage().hasText()) {
                    return true;
                }


                if (update.getMessage().hasPhoto()) {
                    List<PhotoSize> photos = update.getMessage().getPhoto();
                    PhotoSize photo = photos.size() == 2 ? photos.get(1) : photos.get(0);
                    String filePath = prepareBase64(photo.getFileId(), true);
                    String base64 = getBase64(filePath);
                    System.out.println("Photo");

                    return true;

                }

                if (update.getMessage().hasDocument()) {
                    Document document = update.getMessage().getDocument();
                    String base64 = prepareBase64(document.getFileId(), false);
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

