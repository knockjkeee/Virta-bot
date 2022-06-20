package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.Command;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;
import ru.newsystems.basecore.utils.ReceivedText;

@Service
public class CommandService implements ReceivedUpdate {

    private final VirtaBot bot;
    private final ReceivedText receivedText;


    public CommandService(VirtaBot bot, ReceivedText receivedText) {
        this.bot = bot;
        this.receivedText = receivedText;
    }

    @SneakyThrows
    @Override
    public void received(CommandUpdateDTO cUpdate) {

        String resultText = "";

        switch (cUpdate.getCommand().getCommand()) {
            case START -> resultText = receivedText.getTextFromCommandStart(cUpdate);
            case MY_ID -> resultText = receivedText.getTextFromCommandMyId(cUpdate);
            case REGISTRATION -> resultText = receivedText.getTextFromCommandRegistration(cUpdate);
            case CREATE_TICKER -> resultText = receivedText.getTextFromCommandCreateTicket(cUpdate);
            case HELP -> resultText = receivedText.getTextFromCommandHelp(cUpdate);
        }


        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(cUpdate.getUpdate().getMessage().getChatId()))
                .text(resultText)
                .replyToMessageId(cUpdate.getUpdate().getMessage().getMessageId())
                .build());
    }
}
