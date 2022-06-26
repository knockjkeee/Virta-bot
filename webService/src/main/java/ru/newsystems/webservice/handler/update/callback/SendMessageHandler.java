package ru.newsystems.webservice.handler.update.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.callback.SendCommentDTO;
import ru.newsystems.basecore.model.state.SerializableInlineType;
import ru.newsystems.basecore.model.state.MessageState;

@Component
public class SendMessageHandler extends CallbackUpdateHandler<SendCommentDTO> {
  @Autowired private VirtaBot bot;

  @Override
  protected Class<SendCommentDTO> getDtoType() {
    return SendCommentDTO.class;
  }

  @Override
  protected SerializableInlineType getSerializableType() {
    return SerializableInlineType.SEND_COMMENT;
  }

  @Override
  protected void handleCallback(Update update, SendCommentDTO dto) throws TelegramApiException {
    String text = MessageState.SENDCOMMENT.getName() + " по заявке №" + update.getCallbackQuery().getMessage().getReplyToMessage().getText();
    bot.execute(
            SendMessage.builder()
                    .text(text)
                    .replyToMessageId(update.getCallbackQuery().getMessage().getMessageId())
                    .chatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()))
                    .replyMarkup(ForceReplyKeyboard.builder().forceReply(true).build())
                    .build());
  }
}
