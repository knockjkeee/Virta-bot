package ru.newsystems.webservice.handler.update;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.integration.parser.CommandParser;
import ru.newsystems.basecore.model.dto.ParseDTO;
import ru.newsystems.webservice.utils.ReceivedText;

import java.util.Optional;

import static ru.newsystems.basecore.utils.TelegramUtil.getMessage;

@Component
public class MessageUpdateHandler implements UpdateHandler{

    private final CommandParser commandParser;
    private final ReceivedText receivedText;
    private final VirtaBot bot;

    public MessageUpdateHandler(CommandParser commandParser, ReceivedText receivedText, VirtaBot bot) {
        this.commandParser = commandParser;
        this.receivedText = receivedText;
        this.bot = bot;
    }

    @Override
    public boolean handleUpdate(Update update) throws TelegramApiException {
        Message message = getMessage(update);
        if (message == null) return false;
        String text = message.getText();
        Optional<ParseDTO> command = commandParser.parseCommand(text);
        if (command.isEmpty()) {
            prepareText(update);
            return true;
        }
        return false;
    }

    private void prepareText(Update update) throws TelegramApiException{
        String resultText = receivedText.getTextToMessage(update);
        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(resultText)
                .replyToMessageId(update.getMessage().getMessageId())
                .build());
    }


    @Override
    public UpdateHandlerStage getStage() {
        return UpdateHandlerStage.MESSAGE;
    }

}
