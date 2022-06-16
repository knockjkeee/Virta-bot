package ru.newsystems.basecore.integration;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ISubscriber {
    void handleEvent(Update update);
}
