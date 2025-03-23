package ru.yandex.practicum.intershop.service.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.intershop.configuration.IntershopConfiguration;
import ru.yandex.practicum.intershop.dto.ItemDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.ItemRepositoryJpa;
import ru.yandex.practicum.intershop.utils.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryJpa itemRepository, IntershopConfiguration intershopConfiguration) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<ItemDto> getItems(String search, Pageable pageable) {
        Page<Item> page;
        if (StringUtils.isNullOrBlank(search)) {
            page = itemRepository.findAll(pageable);
        } else {
            String searchPattern = "%" + search.trim() + "%";
            page = itemRepository.findAllByTitleIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(searchPattern, searchPattern, pageable);
        }

        List<ItemDto> itemDtos = page.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();

        return new PageDto<>(itemDtos,
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long id) {
        return ItemMapper.mapToItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Товар с id " + id + " не найден")));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getItemCount() {
        return itemRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<byte[]> findImageByPostId(long itemId) {
        return itemRepository.findImageByItemId(itemId);
    }
}
