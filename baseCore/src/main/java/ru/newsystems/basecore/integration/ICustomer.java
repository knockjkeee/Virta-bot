package ru.newsystems.basecore.integration;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ICustomer {
    void subscribe(ISubscriber o);
    void unsubscribe(ISubscriber o);
    void notificationObserver(Update update);
}
