package ru.newsystems.basecore.integration;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Subscriber {
    void handleEvent(Update update);
}
