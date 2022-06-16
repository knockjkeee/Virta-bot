package ru.newsystems.webservice.integration;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.webservice.service.UpdateReceiveService;

import java.util.List;

@Component
public class VirtaBot extends TelegramLongPollingBot {

    private final UpdateReceiveService onUpdateReceive;

    public VirtaBot(UpdateReceiveService onUpdateReceive) {
        this.onUpdateReceive = onUpdateReceive;
    }

    @Override
    public String getBotUsername() {
        return "TestBotApi";
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @Override
    public void onUpdateReceived(Update update) {
        onUpdateReceive.receive(update, this);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
