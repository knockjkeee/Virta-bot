package ru.newsystems.basecore.integration;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Customer {
    void subscribe(Subscriber o);
    void unsubscribe(Subscriber o);
    void notificationSubscribers(Update update);
}
