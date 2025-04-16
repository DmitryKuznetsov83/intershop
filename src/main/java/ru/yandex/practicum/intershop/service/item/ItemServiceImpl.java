package ru.yandex.practicum.intershop.service.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.CartItem;
import ru.yandex.practicum.intershop.repository.cart.CartRepository;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;
import ru.yandex.practicum.intershop.utils.StringUtils;

import java.util.NoSuchElementException;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public Mono<PageDto<ItemDto>> getItems(Pageable pageable, String search) {
        return itemRepository.findAll(pageable, search).map(ItemMapper.INSTANCE::mapToItemDto)
                .collectList()
                .zipWith(StringUtils.isNullOrBlank(search) ? getItemCount() : getItemCount(search))
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()))
                .map(p -> new PageDto<>(p.stream().toList(),
                        p.getTotalPages(),
                        p.getTotalElements(),
                        p.hasNext(),
                        p.hasPrevious()));
    }

    @Override
    public Mono<ItemDto> getItemById(Long id) {
        return Mono.zip(itemRepository.findById(id)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Товар с id " + id + " не найден"))),
                        cartRepository.findById(id)
                                .defaultIfEmpty(CartItem.getEmptyCartPosition()))
                .map(tuple -> {
                    ItemDto itemDto = ItemMapper.INSTANCE.mapToItemDto(tuple.getT1());
                    itemDto.setQuantity(tuple.getT2().getQuantity());
                    return itemDto;
                });

    }

    @Override
    public Mono<Long> getItemCount() {
        return itemRepository.count();
    }

    @Override
    public Mono<Long> getItemCount(String search) {
        return itemRepository.countWithSearch("%" + search + "%");
    }

    @Override
    public Mono<byte[]> findImageByPostId(long itemId) {
        return itemRepository.findImageByItemId(itemId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Картинка для товара с id " + itemId + " не найдена")));
    }
}
