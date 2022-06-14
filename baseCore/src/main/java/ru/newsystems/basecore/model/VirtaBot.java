package ru.newsystems.basecore.model;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.function.Consumer;

@Component
public class VirtaBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "Test";
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        System.out.println(text);
        SendMessage snd = new SendMessage();
        snd.setText(text);
        snd.setChatId(String.valueOf(update.getMessage().getChatId()));
        snd.setReplyToMessageId(update.getMessage().getMessageId());
        execute(snd);
    }



    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
