package ru.newsystems.webservice.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.state.Command;

@Component
public class HelpCommandHandler implements CommandHandler {

    private final VirtaBot bot;

    public HelpCommandHandler(VirtaBot bot) {
        this.bot = bot;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        bot.execute(
                SendMessage.builder()
                        .text("<pre>HELP</pre>\n<u>HELP</u>\n<i>HELP</i>\n<b>HELP</b>")
                        .parseMode(ParseMode.HTML)
                        .chatId(message.getChatId().toString())
                        .build());
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }
}
