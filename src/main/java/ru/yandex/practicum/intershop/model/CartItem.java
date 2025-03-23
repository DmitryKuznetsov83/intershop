package ru.yandex.practicum.intershop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="cart_item")
@Getter
@Setter
@NoArgsConstructor
public class CartItem {

    @Id
    private Long item_id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    private Integer quantity;

}
