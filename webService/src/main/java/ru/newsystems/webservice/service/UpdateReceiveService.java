package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.integration.ISubscriber;
import ru.newsystems.basecore.integration.VirtaBot;

@Service
public class UpdateReceiveService implements ISubscriber {

    private final VirtaBot bot;

    public UpdateReceiveService(VirtaBot bot) {
        bot.subscribe(this);
        this.bot = bot;
    }

    @SneakyThrows
    @Override
    public void handleEvent(Update update) {
        String text = update.getMessage().getText();
        //update.getMessage().from.getId()
        bot.execute(SendMessage.builder()
                            .chatId(String.valueOf(update.getMessage().getChatId()))
                            .text(text)
                            .replyToMessageId(update.getMessage().getMessageId())
                            .build());
    }
}

