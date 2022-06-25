package ru.newsystems.webservice.handler.messaga;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;

@Component
public class MessageSendCommentHandler implements MessageHandler{

    private final MessageLocalRepo localRepo;
    private final VirtaBot bot;

    public MessageSendCommentHandler(MessageLocalRepo localRepo, VirtaBot bot) {
        this.localRepo = localRepo;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (MessageState.getState(text).equals(MessageState.SENDCOMMENT)) {
            return true;
        } else {
            return false;
        }
    }
}