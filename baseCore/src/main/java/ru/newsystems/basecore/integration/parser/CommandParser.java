package ru.newsystems.basecore.integration.parser;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;
import ru.newsystems.basecore.model.dto.Command;
import ru.newsystems.basecore.model.dto.ParseDTO;
import ru.newsystems.basecore.utils.StringUtil;

import java.util.HashMap;
import java.util.Optional;

@Component
public class CommandParser implements Parser {

    private final String PREFIX_FOR_COMMAND = "/";
//    private final String DELIMITER_COMMAND_BOTNAME = "@";

    @Override
    public Optional<ParseDTO> parseCommand(String msg) {
        if (StringUtil.isBlank(msg)) {
            return Optional.empty();
        }
        String trimText = StringUtil.trim(msg);
        ImmutablePair<String, String> commandAndText = getDelimitedCommandFromText(trimText);
        if (isCommand(commandAndText.getKey())) {
            //String commandForParse = cutCommandFromFullText(commandAndText.getKey());
            Optional<Command> command = Command.parseCommand(commandAndText.getKey());
            return command.map(c -> new ParseDTO(c, commandAndText.getValue()));
        }
        return Optional.empty();
    }


    private ImmutablePair<String, String> getDelimitedCommandFromText(String trimText) {
        ImmutablePair<String, String> commandText;
        if (trimText.contains(" ")) {
            int indexOfSpace = trimText.indexOf(" ");
            commandText = new ImmutablePair<>(trimText.substring(0, indexOfSpace), trimText.substring(indexOfSpace + 1));
        } else commandText = new ImmutablePair<>(trimText, "");
        return commandText;
    }

//    private String cutCommandFromFullText(String text) {
//        return text.contains(DELIMITER_COMMAND_BOTNAME)
//                ? text.substring(1, text.indexOf(DELIMITER_COMMAND_BOTNAME))
//                : text.substring(1);
//    }

    private boolean isCommand(String text) {
        return text.startsWith(PREFIX_FOR_COMMAND);
    }
}
