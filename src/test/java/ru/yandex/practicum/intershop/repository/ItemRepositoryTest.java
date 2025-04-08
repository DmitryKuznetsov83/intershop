package ru.yandex.practicum.intershop.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import ru.yandex.practicum.intershop.SpringBootPostgreSQLBase;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;
import ru.yandex.practicum.intershop.service.initial_loader.InitialLoaderServiceImpl;

import java.util.List;


@DataR2dbcTest
@Import({InitialLoaderServiceImpl.class})
public class ItemRepositoryTest extends SpringBootPostgreSQLBase {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InitialLoaderServiceImpl initialLoaderService;

    @BeforeEach
    public void setUp() {
        databaseClient.sql("TRUNCATE TABLE order_item, orders, cart_item, item CASCADE;").then().block();
        initialLoaderService.load().block();
    }

    @Test
    void cartQuantityTest() {
        List<Item> allItems = itemRepository.findAll().collectList().block();
        Long itemId = allItems.get(0).getId();

        cartRepository.insert(itemId, 3).block();

        Assertions.assertThat(cartRepository.findById(itemId).block())
                .withFailMessage("Должна сохраняться позиция в корзине")
                .isNotNull()
                .withFailMessage("Количество должно быть 3")
                .extracting(CartItem::getQuantity)
                .isEqualTo(3);

    }
}