package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.integration.Subscriber;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;
import ru.newsystems.basecore.model.dto.ParseDTO;

import java.util.Optional;

@Service
public class UpdateReceiveService implements Subscriber {

    private final VirtaBot bot;
    private final CommandParser commandParser;
    private final CommandService commandService;
    private final MessageService messageService;

    public UpdateReceiveService(VirtaBot bot, CommandParser commandParser,
                                CommandService commandService, MessageService messageService) {
        this.commandService = commandService;
        this.messageService = messageService;
        bot.subscribe(this);
        this.bot = bot;
        this.commandParser = commandParser;
    }

    @SneakyThrows
    @Override
    public void handleEvent(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            Optional<ParseDTO> parseTextToCommand = commandParser.parseCommand(text);
            if (parseTextToCommand.isPresent()) {
                commandService.received(new CommandUpdateDTO(update, parseTextToCommand.get()));
            } else {
                messageService.received(new CommandUpdateDTO(update, new ParseDTO(null, text)));
            }
        }
    }
}

