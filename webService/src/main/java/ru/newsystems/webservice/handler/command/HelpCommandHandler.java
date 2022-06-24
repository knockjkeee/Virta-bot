package ru.newsystems.webservice.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonPollType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.Command;
import ru.newsystems.basecore.model.ReplyKeyboardButton;
import ru.newsystems.basecore.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class HelpCommandHandler implements CommandHandler {

    private final VirtaBot bot;

    public HelpCommandHandler(VirtaBot bot) {
        this.bot = bot;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        KeyboardRow rowText = new KeyboardRow();
        KeyboardRow rowRequest = new KeyboardRow();
        rowText.add("Text 1");
        rowText.add("Text 2");
        rowRequest.add(KeyboardButton.builder().text("Contact").requestContact(true).build());
        rowRequest.add(KeyboardButton.builder().text("Location").requestLocation(true).build());
        rowRequest.add(
                KeyboardButton.builder().text("Poll").requestPoll(new KeyboardButtonPollType()).build());

        bot.execute(
                SendMessage.builder()
                        .text("This message contains ReplyKeyboardMarkup")
                        .chatId(message.getChatId().toString())
                        .replyMarkup(
                                ReplyKeyboardMarkup.builder()
                                        .resizeKeyboard(true)
                                        .keyboardRow(rowText)
                                        .keyboardRow(rowRequest)
                                        .build())
                        .build());
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }
}
