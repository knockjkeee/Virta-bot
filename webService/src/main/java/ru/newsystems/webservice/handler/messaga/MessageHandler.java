package ru.newsystems.webservice.handler.messaga;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface MessageHandler {
    boolean handleUpdate(Update update) throws TelegramApiException;
}
