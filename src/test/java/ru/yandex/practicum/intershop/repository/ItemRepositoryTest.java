package ru.yandex.practicum.intershop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.SpringBootPostgreSQLBase;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.service.initial_loader.InitialLoaderServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@Import({InitialLoaderServiceImpl.class, ItemRepositoryJdbc.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ItemRepositoryTest extends SpringBootPostgreSQLBase {

    @Autowired
    private ItemRepositoryJpa itemRepositoryJpa;

    @Autowired
    private InitialLoaderServiceImpl initialLoaderService;

    @BeforeEach
    public void setUp() {
        initialLoaderService.load();
    }

    @Test
    @Transactional
    void cartQuantityTest() {
        List<Item> allItems = itemRepositoryJpa.findAll();
        Item item = allItems.get(1);

        // позиции нет в корзине
        assertThat(item.getCartQuantity(), is(0));

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(3);
        cartItem.setItem(item);
        item.setCartItem(cartItem);
        itemRepositoryJpa.save(item);

        // в корзине 3 штуки
        assertThat(item.getCartQuantity(), is(3));

    }

}
