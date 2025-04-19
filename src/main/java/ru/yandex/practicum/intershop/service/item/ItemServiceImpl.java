package ru.yandex.practicum.intershop.service.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.utils.StringUtils;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemCacheService itemCacheService;
    private final CartRepository cartRepository;

    @Autowired
    public ItemServiceImpl(ItemCacheService itemCacheService, CartRepository cartRepository) {
        this.itemCacheService = itemCacheService;
        this.cartRepository = cartRepository;
    }

    @Override
    public Mono<PageDto<ItemDto>> getItems(Pageable pageable, String searchRaw) {
        String search;
        if (StringUtils.isNullOrBlank(searchRaw)) {
            search = null;
        } else {
            search = searchRaw.trim().toLowerCase();
        }
        return itemCacheService.findAll(pageable, search)
                .map(ItemMapper.INSTANCE::mapToItemDto)
                .collectList()
                .flatMap(itemDtoList -> {
                    return cartRepository
                            .findAllById(itemDtoList
                                    .stream()
                                    .map(ItemDto::getId)
                                    .toList())
                            .collectList()
                            .flatMap(cartItems -> {
                                for (ItemDto itemDto : itemDtoList) {
                                    for (CartItem cartItem : cartItems) {
                                        if (itemDto.getId().equals(cartItem.getItemId())) {
                                            itemDto.setQuantity(cartItem.getQuantity());
                                        }
                                    }
                                }
                                return Mono.just(itemDtoList);
                            });
                })
                .zipWith(search == null? getItemCount(false) : getItemCount(search))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(p -> new PageDto<>(p.stream().toList(),
                        p.getTotalPages(),
                        p.getTotalElements(),
                        p.hasNext(),
                        p.hasPrevious()));
    }

    @Override
    public Mono<ItemDto> getItemById(Long id) {
        return Mono.zip(itemCacheService.getItemById(id),
                        cartRepository.findById(id)
                                .defaultIfEmpty(CartItem.getEmptyCartPosition()))
                .map(tuple -> {
                    ItemDto itemDto = ItemMapper.INSTANCE.mapToItemDto(tuple.getT1());
                    itemDto.setQuantity(tuple.getT2().getQuantity());
                    return itemDto;
                });
    }

    @Override
    public Mono<Long> getItemCount(boolean forceCacheRefresh) {
        return itemCacheService.count(forceCacheRefresh);
    }

    @Override
    public Mono<Long> getItemCount(String search) {
        return itemCacheService.countWithSearch(search.trim().toLowerCase());
    }

    @Override
    public Mono<byte[]> findImageByPostId(long itemId) {
        return itemCacheService.findImageByPostId(itemId);
    }

}
