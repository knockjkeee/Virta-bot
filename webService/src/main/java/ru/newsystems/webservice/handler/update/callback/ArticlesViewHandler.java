package ru.newsystems.webservice.handler.update.callback;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.model.dto.callback.ArticlesViewDTO;
import ru.newsystems.basecore.model.state.SerializableInlineType;

@Component
public class ArticlesViewHandler extends CallbackUpdateHandler<ArticlesViewDTO>{
    @Override
    protected Class<ArticlesViewDTO> getDtoType() {
        return ArticlesViewDTO.class;
    }

    @Override
    protected SerializableInlineType getSerializableType() {
        return SerializableInlineType.ARTICLES_VIEW;
    }

    @Override
    protected void handleCallback(Update update, ArticlesViewDTO dto) throws TelegramApiException {
        System.out.println("articles");
    }
}
