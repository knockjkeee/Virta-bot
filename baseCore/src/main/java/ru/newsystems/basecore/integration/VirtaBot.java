package ru.newsystems.basecore.integration;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
//import ru.newsystems.webservice.service.UpdateReceiveService;

import java.util.ArrayList;
import java.util.List;

@Component
public class VirtaBot extends TelegramLongPollingBot implements ICustomer {

//    private final UpdateReceiveService onUpdateReceive;
//
//    public VirtaBot(UpdateReceiveService onUpdateReceive) {
//        this.onUpdateReceive = onUpdateReceive;
//    }

    List<ISubscriber> observes = new ArrayList<>();


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
//        onUpdateReceive.receive(update, this);
        notificationObserver(update);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public void subscribe(ISubscriber o) {
        observes.add(o);
    }

    @Override
    public void unsubscribe(ISubscriber o) {
        observes.remove(o);
    }

    @Override
    public void notificationObserver(Update update) {
        observes.forEach(e -> e.handleEvent(update));
    }
}
