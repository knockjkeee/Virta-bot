package ru.newsystems.webservice.handler.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.state.Command;

@Component
public class AboutCommandHandler implements CommandHandler {

    private final VirtaBot bot;

    public AboutCommandHandler(VirtaBot bot) {
        this.bot = bot;
    }

    @Override
    public void handleCommand(Message message, String text) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(message.getChatId().toString())
                .parseMode("html")
                .text("Этот бот демонстрирует большинство возможностей телеграмм ботов.\n\n"
                        + "Подписывайтесь на наши соцсети:\n\n"
                        + "Youtube: https://www.youtube.com/channel/UCUFaGUhKgAECPuMZJxXwdbg\n"
                        + "Instagram: https://www.instagram.com/mjc.talks/\n"
                        + "MJC Telegram Chat: https://t.me/mjcby\n"
                        + "Community page: https://mjc.by")
                .build());
    }

    @Override
    public Command getCommand() {
        return Command.ABOUT;
    }
}
