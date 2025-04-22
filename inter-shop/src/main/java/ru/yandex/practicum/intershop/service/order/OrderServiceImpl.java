package ru.yandex.practicum.intershop.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.client.InterPaymentClient;
import ru.yandex.practicum.dto.LackOfFundsException;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.exception.EmptyCartException;
import ru.yandex.practicum.intershop.repository.order.OrderItemProjection;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.Order;
import ru.yandex.practicum.intershop.model.OrderItem;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.order.OrderItemRepository;
import ru.yandex.practicum.intershop.repository.order.OrderRepository;
import ru.yandex.practicum.intershop.service.cart.CartService;
import ru.yandex.practicum.intershop.service.cart.CartState;

import java.rmi.ServerError;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final TransactionalOperator transactionalOperator;
    private final InterPaymentClient interPaymentClient;
    private final CartRepository cartRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartService cartService, TransactionalOperator transactionalOperator, InterPaymentClient interPaymentClient, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.transactionalOperator = transactionalOperator;
        this.interPaymentClient = interPaymentClient;
        this.cartRepository = cartRepository;
    }

    @Override
    public Flux<OrderDto> getOrders() {
        return orderRepository.findAllOrderItems()
                .groupBy(OrderItemProjection::getOrder_id)
                .concatMap(groupedFlux ->
                        groupedFlux
                                .collectList()
                                .map(orderItems -> {
                                    Long orderId = orderItems.get(0).getOrder_id();
                                    List<ItemDto> products = orderItems.stream()
                                            .map(ItemMapper.INSTANCE::mapToItemDto)
                                            .toList();
                                    return new OrderDto(orderId, products);
                                })
                );
    }

    @Override
    public Mono<OrderDto> getOrderById(Long id) {
        return orderRepository.findAllOrderItemsByOrderId(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Заказ с id " + id + " не найден")))
                .collectList()
                .flatMap(orderItems -> {
                    OrderDto orderDto = new OrderDto();
                    orderDto.setId(id);
                    orderDto.setItems(orderItems.stream().map(ItemMapper.INSTANCE::mapToItemDto).toList());
                    return Mono.just(orderDto);
                });
    }

    @Override
    public Mono<Long> createOrder() {

        return cartService.getCartState()
                .flatMap(cartState -> {
                    if (!cartState.isPaymentServiceAvailable()) {
                        return Mono.error(new IllegalStateException("Платёжный сервис недоступен"));
                    }
                    if (!cartState.isPurchaseIsPossible()) {
                        return Mono.error(new LackOfFundsException("Недостаточно средств на счету"));
                    }
                    return Mono.just(cartState);
                })
                .map(CartState::getCartSum)
                .flatMap(sum -> interPaymentClient.payment(sum)
                        .then(transactionalOperator.transactional(
                                cartService.getCartItems()
                                        .switchIfEmpty(Mono.error(new EmptyCartException()))
                                        .collectList()
                                        .flatMap(cartItems -> {
                                            Order newOrder = new Order();
                                            return orderRepository.save(newOrder)
                                                    .flatMap(order -> {
                                                        List<OrderItem> orderItems = cartItems.stream()
                                                                .map(cartItem -> {
                                                                    OrderItem orderItem = new OrderItem();
                                                                    orderItem.setOrderId(order.getId());
                                                                    orderItem.setItemId(cartItem.getId());
                                                                    orderItem.setQuantity(cartItem.getQuantity());
                                                                    return orderItem;
                                                                })
                                                                .toList();
                                                        return orderItemRepository.saveAll(orderItems)
                                                                .then(cartService.clearCart())
                                                                .then(Mono.just(order.getId()));
                                                    });
                                        })
                        ))
                )
                .onErrorResume(e -> {
                    if (e instanceof LackOfFundsException || e instanceof IllegalStateException) {
                        return Mono.error(e);
                    }
                    return cartService.getCartState()
                            .map(CartState::getCartSum)
                            .flatMap(sum -> interPaymentClient.topUp(sum)
                                    .then(Mono.error(e)));
                });
    }
}
