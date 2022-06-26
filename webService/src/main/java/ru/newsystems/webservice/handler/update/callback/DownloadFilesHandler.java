package ru.newsystems.webservice.handler.update.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Attachment;
import ru.newsystems.basecore.model.domain.TicketJ;
import ru.newsystems.basecore.model.dto.callback.DownloadFilesDTO;
import ru.newsystems.basecore.model.state.SerializableInlineType;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class DownloadFilesHandler extends CallbackUpdateHandler<DownloadFilesDTO> {

    private final MessageLocalRepo localRepo;
    private final VirtaBot bot;

    public DownloadFilesHandler(MessageLocalRepo localRepo, VirtaBot bot) {
        this.localRepo = localRepo;
        this.bot = bot;
    }

    @Override
    protected Class<DownloadFilesDTO> getDtoType() {
        return DownloadFilesDTO.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.DOWNLOAD;
    }

    @Override
    protected void handleCallback(Update update, DownloadFilesDTO dto) throws TelegramApiException {
        Long id = update.getCallbackQuery().getMessage().getChatId();
        TicketJ ticket = localRepo.get(id).getTicket();
        List<Attachment> attachments = ticket.getArticles().get(ticket.getArticles().size() - 1).getAttachments();
        bot.execute(SendChatAction.builder()
                .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                .action(ActionType.UPLOADDOCUMENT.toString())
                .build());
        if (attachments.size() != 0) {
            attachments.forEach(e -> {
                try {
                    prepareFileToSend(update, e);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void prepareFileToSend(Update update, Attachment e) throws TelegramApiException {
        byte[] decode = Base64.getDecoder().decode(e.getContent().getBytes(StandardCharsets.UTF_8));
        bot.execute(SendDocument.builder()
                .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                .document(new InputFile(new ByteArrayInputStream(decode), e.getFilename()))
                .build());
    }
}
