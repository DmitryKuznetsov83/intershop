package ru.yandex.practicum.intershop.service.item;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.configuration.CacheConfiguration;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemCacheService {

    private final ItemRepository itemRepository;
    private final ReactiveRedisTemplate<String, Object> jsonSerializerRedisTemplate;
    private final ReactiveRedisTemplate<String, List<Item>> itemListRedisTemplate;

    public ItemCacheService(ItemRepository itemRepository, ReactiveRedisTemplate<String, Object> jsonSerializerRedisTemplate, ReactiveRedisTemplate<String, List<Item>> itemListRedisTemplate) {
        this.itemRepository = itemRepository;

        this.jsonSerializerRedisTemplate = jsonSerializerRedisTemplate;
        this.itemListRedisTemplate = itemListRedisTemplate;
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

    public Flux<Item> findAll(Pageable pageable, String search) {
        String cacheKey = CacheConfiguration.REDIS_ITEM_SEARCH_CACHE
                + "::" + pageable.getPageNumber()
                + "::" + pageable.getPageSize()
                + "::" + pageable.getSort().toString().replaceAll("\\s", "").replaceAll(":", "")
                + (search == null ? "" : "::" + search);

        return itemListRedisTemplate.opsForValue().get(cacheKey)
                .cast(List.class)
                .flatMapMany(list -> Flux.fromIterable((List<Item>) list))
                .switchIfEmpty(
                        itemRepository.findAll(pageable, search)
                                .collectList()
                                .flatMap(items -> itemListRedisTemplate.opsForValue()
                                        .set(cacheKey, items, Duration.ofMinutes(1))
                                        .thenReturn(items))
                                .flatMapMany(list -> Flux.fromIterable((List<Item>) list)
                                ));
    }

    public Mono<Long> count(boolean forceCacheRefresh) {
        if (forceCacheRefresh) {
            return itemRepository.count();
        } else {
            String cacheKey = CacheConfiguration.REDIS_ITEM_SEARCH_COUNT_CACHE;
            return jsonSerializerRedisTemplate.opsForValue().get(cacheKey)
                    .map(value -> ((Number)value).longValue())
                    .switchIfEmpty(itemRepository.count()
                            .flatMap(count -> jsonSerializerRedisTemplate.opsForValue()
                            .set(cacheKey, count, Duration.ofMinutes(1))
                            .thenReturn(count)));
        }
    }

    public Mono<Long> countWithSearch(String search) {
        String cacheKey = CacheConfiguration.REDIS_ITEM_SEARCH_COUNT_CACHE + (search == null ? "" : "::" + search);

        return jsonSerializerRedisTemplate.opsForValue().get(cacheKey)
                .map(value -> ((Number)value).longValue())
                .switchIfEmpty(itemRepository.countWithSearch("%" + search + "%")
                        .flatMap(count -> jsonSerializerRedisTemplate.opsForValue()
                        .set(cacheKey, count, Duration.ofMinutes(1))
                        .thenReturn(count)));
    }
}
