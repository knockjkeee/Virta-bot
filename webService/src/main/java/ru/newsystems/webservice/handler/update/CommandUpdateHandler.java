package ru.newsystems.webservice.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.webservice.handler.command.CommandHandler;
import ru.newsystems.webservice.handler.command.CommandHandlerFactory;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.basecore.model.Command;
import ru.newsystems.basecore.model.dto.ParseDTO;

import java.util.Optional;

import static ru.newsystems.basecore.utils.TelegramUtil.getMessage;

@Component
public class CommandUpdateHandler implements UpdateHandler{

    private final CommandParser commandParser;

    private final CommandHandlerFactory commandHandlerFactory;

    public CommandUpdateHandler(CommandParser commandParser, CommandHandlerFactory commandHandlerFactory) {
        this.commandParser = commandParser;
        this.commandHandlerFactory = commandHandlerFactory;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        Message message = getMessage(update);
        if (message == null) return false;
        String text = message.getText();
        Optional<ParseDTO> command = commandParser.parseCommand(text);
        if (command.isEmpty()) {
            return false;
        }
        handleCommand(update, command.get().getCommand(), command.get().getText());
        return true;
    }

    private void handleCommand(Update update, Command command, String text)
            throws TelegramApiException {
        CommandHandler commandHandler = commandHandlerFactory.getHandler(command);
        commandHandler.handleCommand(update.getMessage(), text);
    }

    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.COMMAND;
    }
}
