package ru.yandex.practicum.intershop.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.intershop.SpringBootPostgreSQLBase;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.ItemRepositoryJpa;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.initial_loader.InitialLoaderServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class IntegrationTest extends SpringBootPostgreSQLBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InitialLoaderServiceImpl initialLoaderService;

    @MockitoSpyBean
    private ItemRepositoryJpa itemRepositoryJpa;

    @MockitoSpyBean
    private CartService cartService;

    @BeforeEach
    void setUp() {
        initialLoaderService.load();
    }

    @Test
    public void addToCart() throws Exception {

        // возьмем id какого-то товар из каталога
        Long itemId = itemRepositoryJpa.findAll().getFirst().getId();

        // добавим товар в корзину
        MvcResult result = mockMvc.perform(post("/items/" + itemId)
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/"+ itemId))
                .andReturn();

        // проверим вызовы
        verify(cartService, times(1)).changeCart(itemId, CartAction.PLUS);
        verify(itemRepositoryJpa, times(1)).save(any(Item.class));

        // redirect URL
        String redirectedUrl = result.getResponse().getRedirectedUrl();
        mockMvc.perform(get(redirectedUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));

    }

}
