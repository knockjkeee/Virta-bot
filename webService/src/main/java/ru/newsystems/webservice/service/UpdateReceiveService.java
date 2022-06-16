package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.integration.Subscriber;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.basecore.model.dto.ParseDTO;

import java.util.Optional;

@Service
public class UpdateReceiveService implements Subscriber {

    private final VirtaBot bot;
    private final CommandParser commandParser;

    public UpdateReceiveService(VirtaBot bot, CommandParser commandParser) {
        bot.subscribe(this);
        this.commandParser = commandParser;
        this.bot = bot;
    }

    @SneakyThrows
    @Override
    public void handleEvent(Update update) {
        String text = update.getMessage().getText();
        Optional<ParseDTO> parseText = commandParser.parseCommand(text);
        //update.getMessage().from.getId()
        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(text)
                .replyToMessageId(update.getMessage().getMessageId())
                .build());
    }
}

