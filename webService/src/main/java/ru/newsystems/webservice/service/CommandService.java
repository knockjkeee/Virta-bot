package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;

@Service
public class CommandService implements ReceivedUpdate {

    private final VirtaBot bot;

    public CommandService(VirtaBot bot) {
        this.bot = bot;
    }

    @SneakyThrows
    @Override
    public void received(CommandUpdateDTO cUpdate) {
        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(cUpdate.getUpdate().getMessage().getChatId()))
                .text("CommandService " + cUpdate.getCommand().getText())
                .replyToMessageId(cUpdate.getUpdate().getMessage().getMessageId())
                .build());
    }
}
