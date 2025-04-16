package ru.yandex.practicum.intershop.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.intershop.SpringBootPostgreSQLBase;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.initial_loader.InitialLoaderServiceImpl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@AutoConfigureWebTestClient
public class IntegrationTest extends SpringBootPostgreSQLBase {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private InitialLoaderServiceImpl initialLoaderService;

    @Autowired
    private ItemRepository itemRepository;

    @MockitoSpyBean
    private CartService cartService;

    @MockitoSpyBean
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        databaseClient.sql("TRUNCATE TABLE order_item, orders, cart_item, item CASCADE;").then().block();
        initialLoaderService.load().block();
    }

    @Test
    public void addToCart() throws Exception {

        // возьмем id какого-то товар из каталога
        Long itemId = itemRepository.findAll()
                .next().block().getId();

        // добавим товар в корзину
        webTestClient.post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/items/" + itemId)
                                .queryParam("action", "PLUS")
                                .build())
                .exchange()
                .expectStatus().is3xxRedirection();

        // проверим вызовы
        verify(cartService, times(1)).changeCart(itemId, CartAction.PLUS);
        verify(cartRepository, times(1)).findById(itemId);
        verify(cartRepository, times(1)).insert(itemId, 1);

    }

}