package ru.newsystems.webservice.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.state.Command;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
        rowText.add("Отправить сообщения");
        rowText.add("Загрузить файлы");
        rowRequest.add(KeyboardButton.builder().text("Закрыть").requestContact(true).build());

        bot.execute(SendMessage.builder()
                .text("Выбирете действие")
                .chatId(message.getChatId().toString())
                .replyMarkup(ReplyKeyboardMarkup.builder()
                        .resizeKeyboard(true)
                        .keyboardRow(rowText)
                        .keyboardRow(rowRequest)
                        .build())
                .build());

        //TODO send document
        byte[] decode = Base64.getDecoder().decode("MTIzMTIzMTI=".getBytes(StandardCharsets.UTF_8));
        bot.execute(SendDocument.builder().chatId(message.getChatId().toString())
                .document(new InputFile(new ByteArrayInputStream(decode), "text.txt")).build());
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }
}
