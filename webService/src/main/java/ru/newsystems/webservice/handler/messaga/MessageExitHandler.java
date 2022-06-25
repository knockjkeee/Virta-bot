package ru.newsystems.webservice.handler.messaga;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.state.MessageState;
import ru.newsystems.basecore.repo.local.MessageLocalRepo;

@Component
public class MessageExitHandler implements MessageHandler{

    private final MessageLocalRepo localRepo;
    private final VirtaBot bot;

    public MessageExitHandler(MessageLocalRepo localRepo, VirtaBot bot) {
        this.localRepo = localRepo;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        String text = update.getMessage().getText();
        if (MessageState.getState(text).equals(MessageState.EXIT)) {
            bot.execute(SendMessage.builder()
                    .text("✅ Выполнено")
                    .replyToMessageId(update.getMessage().getMessageId())
                    .chatId(update.getMessage().getChatId().toString())
                    .replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build())
                    .build());
            localRepo.remove(update.getMessage().getChatId());
            return true;
        } else {
            return false;
        }
    }
}