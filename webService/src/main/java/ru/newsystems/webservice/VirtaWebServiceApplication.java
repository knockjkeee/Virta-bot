package ru.newsystems.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.newsystems.webservice.*"})
public class VirtaWebServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VirtaWebServiceApplication.class, args);
    }

}
