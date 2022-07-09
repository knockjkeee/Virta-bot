package ru.newsystems.webservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.newsystems.basecore.model.dto.domain.TicketGetDTO;
import ru.newsystems.webservice.config.cache.CacheStore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class BaseConfig {
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ScheduledExecutorService getExecutor() {
        return Executors.newScheduledThreadPool(20);
    }

    @Bean
    public CacheStore<TicketGetDTO> ticketGetDTOCache(){
        return new CacheStore<>(5, TimeUnit.MINUTES);
    }
}
