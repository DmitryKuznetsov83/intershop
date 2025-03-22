package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFullDto {

    private Long id;
    private String title;
    private String description;
    private Integer price;
    private Boolean hasImage;

    public String getImgPath() {
        if (hasImage) {
            return "items/" + id + "/image";
        } else {
            return "images/no_image.jpeg";
        }
    }

}