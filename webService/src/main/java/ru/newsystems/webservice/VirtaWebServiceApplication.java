package ru.newsystems.webservice;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PreDestroy;

@SpringBootApplication(scanBasePackages = {"ru.newsystems.webservice.*", "ru.newsystems.basecore.*"})
@EnableJpaRepositories("ru.newsystems.basecore.*")
@EntityScan(basePackages = {"ru.newsystems.basecore.*"})
@Log4j2
public class VirtaWebServiceApplication {

    private final ApplicationContext context;

    public VirtaWebServiceApplication(ApplicationContext context) {
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(VirtaWebServiceApplication.class, args);
    }

    @PreDestroy
    public void onExit() {
        log.info("###STOP FROM THE LIFECYCLE###");
        //ScheduledExecutorService bean = context.getBean(ScheduledExecutorService.class);
        //bean.shutdown();
    }
}

