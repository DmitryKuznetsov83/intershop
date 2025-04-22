package ru.yandex.practicum.intershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.client.InterPaymentClientConfig;

@SpringBootApplication
public class IntershopApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntershopApplication.class, args);
    }

}
