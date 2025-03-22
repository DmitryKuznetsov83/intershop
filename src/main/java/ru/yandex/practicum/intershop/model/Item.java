package ru.yandex.practicum.intershop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

@Entity
@Table(name="item")
@Getter
@NoArgsConstructor
public class Item {

    // Constructor for JPA
    public Item(Long id, String title, String description, Integer price, byte[] image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    // Constructor for JDBC
    public Item(Long id, String title, String description, Integer price, byte[] image, boolean hasImage) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.image = image;
        this.hasImage = hasImage;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Integer price;

    @Column(columnDefinition = "BYTEA")
    @Lazy
    private byte[] image;

    @Column(name = "has_image")
    private boolean hasImage;

    @PrePersist
    @PreUpdate
    public void checkImagePresence() {
        this.hasImage = (image != null && image.length > 0);
    }

}
