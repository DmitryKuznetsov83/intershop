package ru.yandex.practicum.intershop.repository.item;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemWithQuantityProjection {

    private Long id;
    private String title;
    private String description;
    private Integer price;
    private boolean has_image;
    private Integer quantity;

}
