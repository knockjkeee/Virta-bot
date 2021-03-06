package ru.newsystems.webservice.handler.update;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.model.state.UpdateHandlerStage;

public interface UpdateHandler {

    boolean handleUpdate(Update update) throws TelegramApiException;

    UpdateHandlerStage getStage();
}
