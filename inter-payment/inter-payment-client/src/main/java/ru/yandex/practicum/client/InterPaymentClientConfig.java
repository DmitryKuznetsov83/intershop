package ru.yandex.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConditionalOnClass(InterPaymentClient.class)
public class InterPaymentClientConfig {

    @Value("${interpayment.service.url}")
    private String connectionString;


    @Bean
    @ConditionalOnMissingBean
    public InterPaymentClient interPaymentClient() {
        return new InterPaymentClient(WebClient.builder(), connectionString);
    }

}
