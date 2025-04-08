package ru.yandex.practicum.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="cart_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @Column("item_id")
    private Long itemId;

    private Integer quantity;

    public static CartItem getEmptyCartPosition() {
        return new CartItem(null, 0);
    }
}
