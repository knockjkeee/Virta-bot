package ru.newsystems.webservice.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.model.dto.CommandUpdateDTO;

public interface ReceivedUpdate {
    void received(CommandUpdateDTO cUpdate);
}
