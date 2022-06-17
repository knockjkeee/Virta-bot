package ru.newsystems.basecore.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.model.Command;

import java.util.Optional;

@Data
@RequiredArgsConstructor
public class CommandUpdateDTO {
    private final Update update;
    private final ParseDTO command;
}
