package ru.newsystems.basecore.utils;

import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramUtil {

    @Nullable
    public static Message getMessage(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        Message message = update.getMessage();
        if (!message.hasText()) {
            return null;
        }
        return message;
    }
}
