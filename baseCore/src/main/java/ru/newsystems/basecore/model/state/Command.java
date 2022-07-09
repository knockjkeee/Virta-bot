package ru.newsystems.basecore.model.state;

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
    MY_ID("/my_id", "Get information for me"),
    MY_TICKET("/my_ticket", "Get my open tickets"),
    CREATE_TICKET("/create_ticket", "Create new ticket"),
    HELP("/help", "Help at work"),
    ABOUT("/about", "About me");

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
