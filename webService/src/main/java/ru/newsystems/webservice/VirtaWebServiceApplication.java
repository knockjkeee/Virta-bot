package ru.newsystems.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"ru.newsystems.webservice.*", "ru.newsystems.basecore.*"})
@EnableJpaRepositories("ru.newsystems.basecore.*")
@EntityScan(basePackages = {"ru.newsystems.basecore.*"})
public class VirtaWebServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtaWebServiceApplication.class, args);
    }
}
