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
                .text("""
                        Этот бот демонстрирует большинство возможностей
                        взаимодействия с информационной системой\040
                        <b>НИС-про|1.0</b>
                       
                        <u>Контактная информация:</u>
                        Адрес: г. Москва, ул ... дом ...
                        Телефон: +7(499)123-45-67
                        Почта: info@nic-pro.ru
                        Группа в VK: https://wwww.vk.com
                        """)
                .build());
    }

    @Override
    public Command getCommand() {
        return Command.ABOUT;
    }
}
