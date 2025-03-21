package ru.yandex.practicum.intershop.service.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.intershop.configuration.IntershopConfiguration;
import ru.yandex.practicum.intershop.dto.ItemFullDto;
import ru.yandex.practicum.intershop.dto.PageDto;
import ru.yandex.practicum.intershop.mapper.ItemMapper;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.ItemRepositoryJpa;
import ru.yandex.practicum.intershop.utils.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryJpa itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepositoryJpa itemRepository, IntershopConfiguration intershopConfiguration) {
        this.itemRepository = itemRepository;
    }

    @Override
    public PageDto<ItemFullDto> getItems(String search, Pageable pageable) {
        Page<Item> page;
        if (StringUtils.isNullOrBlank(search)) {
            page = itemRepository.findAll(pageable);
        } else {
            String searchPattern = "%" + search.trim() + "%";
            page = itemRepository.findAllByTitleIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(searchPattern, searchPattern, pageable);
        }

        List<ItemFullDto> itemFullDtos = ItemMapper.mapToItemFullDtoList(page.stream().toList());

        return new PageDto<>(itemFullDtos,
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious());
    }

    @Override
    public ItemFullDto getItemById(Long id) {
        return ItemMapper.mapToItemFullDto(itemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Товар с id " + id + " не найден")));
    }

    @Override
    public Long getItemCount() {
        return itemRepository.count();
    }

    @Override
    public void saveItem(ItemFullDto itemFullDto) {

    }
}
