package ru.newsystems.webservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.integration.Subscriber;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.webservice.handler.update.UpdateHandler;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateReceiveService implements Subscriber {

    private final VirtaBot bot;
    private final CommandParser commandParser;
    private final CommandService commandService;
    private final MessageService messageService;


    @Autowired
    private List<UpdateHandler> updateHandlers;

    public UpdateReceiveService(VirtaBot bot, CommandParser commandParser, CommandService commandService, MessageService messageService) {
        bot.subscribe(this);
        this.bot = bot;
        this.commandParser = commandParser;
        this.commandService = commandService;
        this.messageService = messageService;
    }

    @Override
    public void handleEvent(Update update) {
        for (UpdateHandler updateHandler : updateHandlers) {
            try {
                if (updateHandler.handleUpdate(update)) {
                    //TODO log good work
                    return;
                }
                //TODO log bad work, search entity
            } catch (Exception e) {
                //TODO log excpt work
                e.printStackTrace();
            }
        }


//        if (update.hasMessage()) {
//            String text = update.getMessage().getText();
//            Optional<ParseDTO> parseTextToCommand = commandParser.parseCommand(text);
//            if (parseTextToCommand.isPresent()) {
//                commandService.received(new CommandUpdateDTO(update, parseTextToCommand.get()));
//            } else {
//                messageService.received(new CommandUpdateDTO(update, new ParseDTO(null, text)));
//            }
//        }
    }

    @PostConstruct
    public void init() {
        updateHandlers = updateHandlers.stream()
                .sorted(Comparator.comparingInt(u -> u.getStage().getOrder()))
                .collect(Collectors.toList());
    }

}

