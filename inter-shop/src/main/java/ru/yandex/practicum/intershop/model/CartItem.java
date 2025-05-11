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
    private Long id;

    @Column("item_id")
    private Long itemId;

    @Column("user_id")
    private Long userId;

    private Integer quantity;

    public CartItem(Long userId, Long itemId, Integer quantity) {
        this.userId = userId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static CartItem getEmptyCartPosition() {
        return new CartItem(null, null, null, 0);
    }
}
