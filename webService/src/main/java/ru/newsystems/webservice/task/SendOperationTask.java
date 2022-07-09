package ru.newsystems.webservice.task;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.domain.Attachment;
import ru.newsystems.basecore.model.dto.domain.RequestDataDTO;
import ru.newsystems.webservice.service.RestService;

import java.util.ArrayList;
import java.util.List;

import static ru.newsystems.webservice.utils.TelegramUtil.sendCreateTicket;
import static ru.newsystems.webservice.utils.TelegramUtil.sendNewComment;

@Data
@Builder
public class SendOperationTask implements Runnable {
    private RequestDataDTO req;
    private boolean isSendComment;
    private Update update;
    private VirtaBot bot;
    private RestService restService;

    @Override
    public void run() {
        try {
            if (isSendComment) {
                sendNewComment(update, req, restService, bot);
            }else{
                sendCreateTicket(update, req, restService, bot);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void updateAttachment(Attachment attach) {
        List<Attachment> attaches = req.getAttaches();
        List<Attachment> result = new ArrayList<>();
        if (attaches == null && attach != null) {
            result.add(attach);
            req.setAttaches(result);
        } else if (attaches != null){
            result.addAll(attaches);
            result.add(attach);
            req.setAttaches(result);
        }
    }
}
