package ru.newsystems.webservice.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
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
                        .text("This message contains ForceReplyKeyboard")
                        .replyToMessageId(message.getMessageId())
                        .chatId(message.getChatId().toString())
                        .replyMarkup(ForceReplyKeyboard.builder().forceReply(true).build())
                        .build());
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }
}
