package ru.newsystems.basecore.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@RequiredArgsConstructor
public class CommandUpdateDTO {
    private final Update update;
    private final ParseDTO command;
}
