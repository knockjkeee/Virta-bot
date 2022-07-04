package ru.newsystems.webservice.task;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.newsystems.basecore.integration.VirtaBot;
import ru.newsystems.basecore.model.dto.domain.RequestUpdateDTO;

@Data
@RequiredArgsConstructor
@Component
public class SendOperationTask implements Runnable {

    private RequestUpdateDTO req;
    private Update update;
    private VirtaBot bot;


    @Override
    public void run() {

    }






}
