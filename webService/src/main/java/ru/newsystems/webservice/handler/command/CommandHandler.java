package ru.newsystems.webservice.handler.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.model.state.Command;

public interface CommandHandler {
    void handleCommand(Message message, String text) throws TelegramApiException;

    Command getCommand();
}
