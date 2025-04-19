package ru.yandex.practicum.intershop.repository.item;

import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.utils.StringUtils;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final DatabaseClient databaseClient;

    public ItemRepositoryCustomImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<Item> findAll(Pageable pageable, String search) {
        String baseQuery = """
            SELECT i.id, i.title, i.description, i.price, i.has_image
            FROM item i
        """;

        boolean hasSearch = search != null;
        String whereClause = "WHERE lower(i.title) LIKE lower(:searchPattern) OR lower(i.description) LIKE lower(:searchPattern)";

        String orderBy = pageable.getSort().isSorted()
                ? "ORDER BY " + pageable.getSort().stream()
                .map(order -> "i." + order.getProperty() + " " + order.getDirection().name())
                .collect(Collectors.joining(", "))
                : "ORDER BY i.id";

        String pagingClause = "LIMIT :limit OFFSET :offset";

        String finalQuery = baseQuery + "\n" + (hasSearch ? whereClause + "\n" : "") + orderBy + "\n" + pagingClause;

        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(finalQuery)
                .bind("limit", pageable.getPageSize())
                .bind("offset", pageable.getOffset());

        if (hasSearch) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            spec = spec.bind("searchPattern", pattern);
        }

        return spec
                .map((row, meta) -> {
                    Item item = new Item();
                    item.setId(row.get("id", Long.class));
                    item.setTitle(row.get("title", String.class));
                    item.setDescription(row.get("description", String.class));
                    item.setPrice(row.get("price", Integer.class));
                    item.setHasImage(row.get("has_image", Boolean.class));
                    return item;
                })
                .all();
    }

    @Override
    public Mono<byte[]> findImageByItemId(Long itemId) {
        return databaseClient.sql("SELECT image FROM item WHERE id = :id")
                .bind("id", itemId)
                .map((row, metadata) -> {
                    ByteBuffer buffer = row.get("image", ByteBuffer.class);
                    if (buffer == null) {
                        return new byte[0];
                    }
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    return bytes;
                })
                .one()
                .filter(b -> Objects.nonNull(b) && b.length > 0);
    }
}
