package ru.yandex.practicum.intershop.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.intershop.emun.Sorting;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "intershop")
@Getter
@Setter
public class IntershopConfiguration {

    private List<Integer> pagingSizeOptions;
    private Integer pagingSizeByDefault;
    private Sorting sortingByDefault;
    private Integer cellInRow;

}