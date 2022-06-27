package ru.newsystems.webservice.handler.messaga;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
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
        String text = update.getMessage().getText();


        if (update.getMessage().getReplyToMessage() != null) {
            List<String> split = Arrays.stream(update.getMessage().getReplyToMessage().getText().split("№"))
                    .map(String::strip)
                    .collect(Collectors.toList());
            if (split.get(0).contains(MessageState.SENDCOMMENT.getName())) {
                //TODO code

//                    byte[] decode = Base64.getDecoder().decode(e.getContent().getBytes(StandardCharsets.UTF_8));
//                    bot.execute(SendDocument.builder()
//                            .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
//                            .document(new InputFile(new ByteArrayInputStream(decode), e.getFilename()))
//                            .build());
                if (update.getMessage().hasPhoto()) {
                    String fileId = update.getMessage().getPhoto().get(1).getFileId();
                    GetFile getFile = new GetFile();
                    getFile.setFileId(fileId);
                    String filePath = bot.execute(getFile).getFilePath();
                    File file = bot.downloadFile(filePath);
                    try {
                        byte[] encode = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
                        System.out.println("Caption: " + file.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                if (update.getMessage().hasDocument()) {
                    Document document = update.getMessage().getDocument();
                    String fileId = document.getFileId();
                    GetFile getFile = new GetFile();
                    getFile.setFileId(fileId);
                    String filePath = bot.execute(getFile).getFilePath();
                    File file = bot.downloadFile(filePath);
                    try {
                        byte[] encode = Base64.getEncoder().encode(Files.readAllBytes(file.toPath()));
                        System.out.println("Document: " + file.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return true;
            }
            return false;
        }
        return false;
    }

}

