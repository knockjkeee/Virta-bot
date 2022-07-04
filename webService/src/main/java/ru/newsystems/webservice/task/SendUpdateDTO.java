package ru.newsystems.webservice.task;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.ScheduledFuture;

@Data
@Builder
public class SendUpdateDTO {
    private ScheduledFuture<?> schedule;
    private SendOperationTask task;

    public void stopSchedule() {
        schedule.cancel(false);
    }
}
