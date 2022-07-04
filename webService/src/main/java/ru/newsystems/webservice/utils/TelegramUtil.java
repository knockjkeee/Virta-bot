package ru.newsystems.webservice.utils;

import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Error;

public class TelegramUtil {

    @Nullable
    public static Message getMessage(Update update) {
        if (!update.hasMessage()) {
            return null;
        }
        Message message = update.getMessage();
        if (!message.hasText() && !message.hasPhoto()) {
            if (!message.hasDocument()) {
                return null;
            }
        }
        return message;
    }

    public static void closeReplyKeyBoard(Update update, VirtaBot bot, boolean isSuccess) throws TelegramApiException {
        String text = isSuccess ? "✅ Выполнено" : "⛔️ В формате \"Заголовок/Сообщение\" ошибка, проверьте рекомендации";
        if (update.hasCallbackQuery()) {
            bot.execute(SendMessage.builder()
                    .text(text)
                    .replyToMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                    .build());
        } else {
            bot.execute(SendMessage.builder()
                    .text(text)
                    .replyToMessageId(update.getMessage().getMessageId())
                    .chatId(update.getMessage().getChatId().toString())
                    //.replyMarkup(ReplyKeyboardRemove.builder().removeKeyboard(true).build())
                    .build());
        }
    }

    public static void sendErrorMsg(VirtaBot bot, Update update, String text, Error error) throws TelegramApiException {
        String resultText = "❗️❗❗ \n<b>ErrorCode</b>: "
                + error.getErrorCode()
                + ""
                + "\n<b>ErrorMessage</b>: "
                + error.getErrorMessage()
                + ""
                + "\nby text: "
                + text;
        if (update.hasCallbackQuery()) {
            bot.execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                    .text(resultText)
                    .parseMode("html")
                    .replyToMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .build());
        } else {
            bot.execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text(resultText)
                    .parseMode("html")
                    .replyToMessageId(update.getMessage().getMessageId())
                    .build());
        }
    }

}
