package ru.yandex.practicum.intershop.repository.item;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.Item;


@Repository
public interface ItemRepository extends ReactiveCrudRepository<Item, Long>, ItemRepositoryCustom {
}
