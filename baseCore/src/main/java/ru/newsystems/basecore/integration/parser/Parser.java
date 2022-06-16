package ru.newsystems.basecore.integration.parser;

import ru.newsystems.basecore.model.dto.ParseDTO;

import java.util.Optional;

public interface Parser {
    Optional<ParseDTO> parseCommand(String msg);
}
