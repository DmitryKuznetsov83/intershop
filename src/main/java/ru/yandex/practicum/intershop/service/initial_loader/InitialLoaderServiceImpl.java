package ru.yandex.practicum.intershop.service.initial_loader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.ItemRepositoryJdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class InitialLoaderServiceImpl implements InitialLoaderService {

    private final ItemRepositoryJdbc itemRepositoryJdbc;

    @Value("${intershop.initial-loader.catalog}")
    private Resource resource;

    public InitialLoaderServiceImpl(ItemRepositoryJdbc itemRepositoryJdbc) {
        this.itemRepositoryJdbc = itemRepositoryJdbc;
    }

    @Override
    public void load() {
        List<String[]> csvRows = getCsvRows();
        List<Item> items = csvRows
                .stream()
                .skip(1)
                .map(r -> new Item(null, r[0], r[1], Integer.parseInt(r[2])))
                .toList();
        itemRepositoryJdbc.saveAll(items);
    }

    @Override
    public Long getItemCount() {
        return (long) getCsvRows().size() - 1;
    }

    private List<String[]> getCsvRows() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.readAll();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

}
