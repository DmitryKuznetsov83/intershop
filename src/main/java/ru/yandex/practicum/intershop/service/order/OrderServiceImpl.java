package ru.yandex.practicum.intershop.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.dto.OrderDto;
import ru.yandex.practicum.intershop.exception.EmptyCartException;
import ru.yandex.practicum.intershop.mapper.OrderMapper;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.model.Order;
import ru.yandex.practicum.intershop.model.OrderItem;
import ru.yandex.practicum.intershop.repository.CartRepositoryJpa;
import ru.yandex.practicum.intershop.repository.OrderRepositoryJpa;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepositoryJpa orderRepositoryJpa;
    private final CartRepositoryJpa cartRepositoryJpa;

    @Autowired
    public OrderServiceImpl(OrderRepositoryJpa orderRepositoryJpa, CartRepositoryJpa cartRepositoryJpa) {
        this.orderRepositoryJpa = orderRepositoryJpa;
        this.cartRepositoryJpa = cartRepositoryJpa;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        return orderRepositoryJpa.findAll()
                .stream()
                .map(OrderMapper.INSTANCE::mapToOrderDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long id) {
        return orderRepositoryJpa.findById(id)
                .map(OrderMapper.INSTANCE::mapToOrderDto)
                .orElseThrow(() -> new NoSuchElementException("Заказ с id " + id + " не найден"));
    }

    @Override
    @Transactional
    public Long createOrder() {
        List<CartItem> allCartItems = cartRepositoryJpa.findAll();
        if (allCartItems.isEmpty()) {
            throw new EmptyCartException();
        }

        Order newOrder = new Order();
        List<OrderItem> orderItems = allCartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(cartItem.getItem());
            orderItem.setOrder(newOrder);
            orderItem.setQuantity(cartItem.getQuantity());
            return orderItem;
        }).toList();

        newOrder.setItems(orderItems);

        Order save = orderRepositoryJpa.save(newOrder);
        allCartItems.stream().map(CartItem::getItem).forEach(item -> item.setCartItem(null));

        return save.getId();
    }
}
