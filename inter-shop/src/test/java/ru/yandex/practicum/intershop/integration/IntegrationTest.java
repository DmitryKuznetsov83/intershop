package ru.yandex.practicum.intershop.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.SpringBootPostgreSQLBase;
import ru.yandex.practicum.intershop.emun.AppUserRole;
import ru.yandex.practicum.intershop.emun.CartAction;
import ru.yandex.practicum.intershop.model.AppUser;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;
import ru.yandex.practicum.intershop.repository.user.AppUserRepository;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.initial_loader.InitialLoaderServiceImpl;
import ru.yandex.practicum.intershop.service.user.AppUserDetails;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.yandex.practicum.intershop.emun.AppUserRole.ROLE_ADMIN;


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

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoSpyBean
    private CartService cartService;

    @MockitoSpyBean
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        databaseClient.sql("TRUNCATE TABLE order_item, orders, cart_item, item, app_user CASCADE;").then().block();

        // 1) Создаём Authentication с ролью ADMIN
        AppUserDetails adminDetails = AppUserDetails.builder()
                .id(0L)
                .login("admin")
                .password(passwordEncoder.encode("admin"))
                .role(ROLE_ADMIN)
                .build();

        Authentication adminAuth =
                new UsernamePasswordAuthenticationToken(
                        adminDetails,
                        null,
                        adminDetails.getAuthorities()
                );

        // 2) Вызываем нужный метод внутри реактивной цепочки
        Mono.defer(() -> {
                    return initialLoaderService.load();
                })
                // 3) Подписываем контекст аутентификации
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(adminAuth))
                .block();

    }

    @Test
    public void addToCart() throws Exception {

        AppUser user = AppUser.builder()
                .login("TestClient")
                .password(passwordEncoder.encode("TestClient"))
                .role(AppUserRole.ROLE_CLIENT)
                .build();
        AppUser savedUser = appUserRepository.save(user).block();
        Long userId = savedUser.getId();

        AppUserDetails userDetails = AppUserDetails.builder()
                .id(userId)
                .login("TestClient")
                .password(passwordEncoder.encode("TestClient"))
                .role(AppUserRole.ROLE_CLIENT)
                .build();

        // Создаём аутентификацию
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // Получаем товар
        Long itemId = itemRepository.findAll().next().block().getId();

        // Подставляем mock-аутентификацию
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockAuthentication(auth))
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items/" + itemId)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection();

        // проверим вызовы
        verify(cartService, times(1)).changeCart(userId, itemId, CartAction.PLUS);
        verify(cartRepository, times(1)).insert(userId, itemId, 1);

    }

}