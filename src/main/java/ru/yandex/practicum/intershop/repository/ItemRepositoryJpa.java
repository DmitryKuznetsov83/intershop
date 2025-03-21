package ru.yandex.practicum.intershop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.Item;


@Repository
public interface ItemRepositoryJpa extends JpaRepository<Item, Long> {

    Page<Item> findAllByTitleIsLikeIgnoreCaseOrDescriptionIsLikeIgnoreCase(String title, String description, Pageable pageable);

}
