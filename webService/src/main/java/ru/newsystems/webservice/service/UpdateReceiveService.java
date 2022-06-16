package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.webservice.integration.VirtaBot;

@Service
public class UpdateReceiveService {

    @SneakyThrows
    public void receive(Update update, VirtaBot bot) {
        String text = update.getMessage().getText();
        //update.getMessage().from.getId()
        bot.execute(SendMessage.builder()
                            .chatId(String.valueOf(update.getMessage().getChatId()))
                            .text(text)
                            .replyToMessageId(update.getMessage().getMessageId())
                            .build());

    }
}

