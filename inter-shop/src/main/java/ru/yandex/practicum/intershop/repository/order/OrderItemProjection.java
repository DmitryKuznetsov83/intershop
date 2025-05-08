package ru.yandex.practicum.intershop.repository.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItemProjection {

    private Long user_id;
    private Long id;
    private Long order_id;
    private Long item_id;
    private String title;
    private String description;
    private Double price;
    private Boolean hasImage;
    private Integer quantity;

}
