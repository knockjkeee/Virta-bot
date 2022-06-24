package ru.newsystems.basecore.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.newsystems.basecore.utils.StringUtil;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Command {

    /*start - Start bot
    my_id - Get information for me
    my_ticket - Get my ticket by user name
    create_ticker - Create new ticket
    help - Help at work
    */
    START("/start", "Description 1"),
    MY_ID("/my_id", "Description 3"),
    MY_TICKET("/my_ticket", "Description 2"),
    CREATE_TICKER("/create_ticker", "Description 4"),
    HELP("/help", "Description 4");

    private final String name;
    private final String desc;

    public static Optional<Command> parseCommand(String command) {
        if (StringUtil.isBlank(command)) {
            return Optional.empty();
        }
        String formatName = StringUtil.trim(command).toLowerCase();
        return Stream.of(values()).filter(c -> c.name.equalsIgnoreCase(formatName)).findFirst();
    }
}
