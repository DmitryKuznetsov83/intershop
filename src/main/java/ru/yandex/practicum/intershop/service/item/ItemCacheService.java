package ru.yandex.practicum.intershop.service.item;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.yandex.practicum.intershop.configuration.CacheConfiguration;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;

import java.util.NoSuchElementException;

@Service
public class ItemCacheService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public ItemCacheService(ItemRepository itemRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    @Cacheable(value = CacheConfiguration.REDIS_ITEM_CACHE, key = "#id")
    public Mono<Item> getItemById(Long id) {
        return itemRepository.findById(id)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Товар с id " + id + " не найден")));
    }

    @Cacheable(value = CacheConfiguration.REDIS_ITEM_IMAGE_CACHE, key = "#itemId")
    public Mono<byte[]> findImageByPostId(long itemId) {
        return itemRepository.findImageByItemId(itemId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Картинка для товара с id " + itemId + " не найдена")));
    }

}
