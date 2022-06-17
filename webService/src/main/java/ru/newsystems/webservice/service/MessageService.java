package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;

@Service
public class MessageService implements ReceivedUpdate{
    private final VirtaBot bot;

    public MessageService(VirtaBot bot) {
        this.bot = bot;
    }

    @SneakyThrows
    @Override
    public void received(CommandUpdateDTO cUpdate) {
        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(cUpdate.getUpdate().getMessage().getChatId()))
                .text("MessageService " + cUpdate.getUpdate().getMessage().getText())
                .replyToMessageId(cUpdate.getUpdate().getMessage().getMessageId())
                .build());
    }
}
