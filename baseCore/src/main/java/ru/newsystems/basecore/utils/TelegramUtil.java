package ru.newsystems.basecore.utils;

import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;

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

    public static void closeReplyKeyBoard(Update update, VirtaBot bot, boolean isSuccess) throws TelegramApiException {
        String text = isSuccess ? "✅ Выполнено" : "⛔️ У комментария отсутствуют прикрепленыы документы";
        bot.execute(SendMessage.builder()
                .text(text)
                .replyToMessageId(update.getMessage().getMessageId())
                .chatId(update.getMessage().getChatId().toString())
                .replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build())
                .build());
    }
}
