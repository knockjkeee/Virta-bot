package ru.newsystems.webservice.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;
import ru.newsystems.webservice.utils.ReceivedText;

@Service
public class MessageService implements ReceivedUpdate{
    private final VirtaBot bot;
    private final ReceivedText receivedText;
    private final RestService restService;

    public MessageService(VirtaBot bot, ReceivedText receivedText, RestService restService) {
        this.bot = bot;
        this.receivedText = receivedText;
        this.restService = restService;
    }

    @SneakyThrows
    @Override
    public void received(CommandUpdateDTO cUpdate) {
        String resultText = receivedText.getTextFromMessage(cUpdate);


//        String text = cUpdate.getCommand().getText();
//
//            id = getId(text);

        bot.execute(SendMessage.builder()
                .chatId(String.valueOf(cUpdate.getUpdate().getMessage().getChatId()))
                .text(resultText)
                .replyToMessageId(cUpdate.getUpdate().getMessage().getMessageId())
                .build());
    }
}
