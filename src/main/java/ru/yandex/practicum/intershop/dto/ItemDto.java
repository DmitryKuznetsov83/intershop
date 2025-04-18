package ru.yandex.practicum.intershop.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemDto {

    private Long id;
    private String title;
    private String description;
    private Integer price;
    private Boolean hasImage;
    private Integer quantity;

    public String returnImgPath() {
        if (hasImage) {
            return "items/" + id + "/image";
        } else {
            return "images/no_image.jpeg";
        }
    }

    public Integer calculateSum() {
        return price * quantity;
    }

}