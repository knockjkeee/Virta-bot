package ru.newsystems.basecore.integration;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class VirtaBot extends TelegramLongPollingBot implements Customer {

    List<Subscriber> subscribers = new ArrayList<>();

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
        notificationSubscribers(update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public void subscribe(Subscriber o) {
        subscribers.add(o);
    }

    @Override
    public void unsubscribe(Subscriber o) {
        subscribers.remove(o);
    }

    @Override
    public void notificationSubscribers(Update update) {
        subscribers.forEach(e -> e.handleEvent(update));
    }
}
