package ru.yandex.practicum.intershop.service.initial_loader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.item.ItemRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class InitialLoaderServiceImpl implements InitialLoaderService {

    private final ItemRepository itemRepository;

    private final ResourceLoader resourceLoader;

    @Value("${intershop.initial-loader.catalog}")
    private Resource resource;

    public InitialLoaderServiceImpl(ItemRepository itemRepository, ResourceLoader resourceLoader) {
        this.itemRepository = itemRepository;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Mono<Void> load() {
        return itemRepository.saveAll(createItems())
                .then();
    }

    @Override
    public Mono<Long> getItemCount() {
        return Mono.just((long) getCsvRows().size() - 1);
    }

    private List<String[]> getCsvRows() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Item> createItems() {
        List<String[]> csvRows = getCsvRows();
        return csvRows
                .stream()
                .skip(1)
                .map(this::stringRowToItem)
                .toList();
    }

    private Item stringRowToItem(String[] row) {
        if (row.length == 4) {
            String filePath = "initial-loader/" + row[3].trim();
            Resource resource = resourceLoader.getResource("classpath:" + filePath);
            if (!resource.exists()) {
                // логируем ошибку
            }
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] image = StreamUtils.copyToByteArray(inputStream);
                return new Item(null, row[0], row[1], Integer.parseInt(row[2]), image, true);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        } else {
            return new Item(null, row[0], row[1], Integer.parseInt(row[2]), null, false);
        }
    }
}
