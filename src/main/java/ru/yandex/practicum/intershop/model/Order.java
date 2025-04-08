package ru.yandex.practicum.intershop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    private Long id;

}
