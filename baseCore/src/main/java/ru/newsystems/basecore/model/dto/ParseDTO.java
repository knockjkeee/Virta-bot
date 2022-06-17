package ru.newsystems.basecore.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.model.Command;

@Data
@RequiredArgsConstructor
public class ParseDTO {
    private final Command command;
    private final String text;
}
