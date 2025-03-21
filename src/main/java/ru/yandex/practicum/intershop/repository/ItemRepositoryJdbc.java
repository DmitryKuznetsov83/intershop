package ru.yandex.practicum.intershop.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.intershop.model.Item;

import java.util.List;

@Repository
public class ItemRepositoryJdbc {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ItemRepositoryJdbc(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void saveAll(List<Item> items) {
        namedParameterJdbcTemplate.batchUpdate(
                "INSERT INTO item (title, description, price) VALUES (:title, :description, :price)",
                SqlParameterSourceUtils.createBatch(items)
        );
    }
}
