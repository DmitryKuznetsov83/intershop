package ru.yandex.practicum.intershop.service.initial_loader;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import ru.yandex.practicum.intershop.model.Item;
import ru.yandex.practicum.intershop.repository.ItemRepositoryJdbc;
import ru.yandex.practicum.intershop.repository.ItemRepositoryJpa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class InitialLoaderServiceImpl implements InitialLoaderService {

    private final ItemRepositoryJdbc itemRepositoryJdbc;

    private final ResourceLoader resourceLoader;

    @Value("${intershop.initial-loader.catalog}")
    private Resource resource;

    public InitialLoaderServiceImpl(ItemRepositoryJdbc itemRepositoryJdbc, ResourceLoader resourceLoader, ItemRepositoryJpa itemRepositoryJpa) {
        this.itemRepositoryJdbc = itemRepositoryJdbc;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void load() {
        List<String[]> csvRows = getCsvRows();
        List<Item> items = csvRows
                .stream()
                .skip(1)
                .map(r -> {
                    if (r.length == 4) {
                        String filePath = "initial-loader/" + r[3].trim();
                        Resource resource = resourceLoader.getResource("classpath:" + filePath);
                        if (!resource.exists()) {
                            // логируем ошибку
                        }
                        try (InputStream inputStream = resource.getInputStream()) {
                            byte[] image = StreamUtils.copyToByteArray(inputStream);
                            return new Item(null, r[0], r[1], Integer.parseInt(r[2]), image, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }

                    } else {
                        return new Item(null, r[0], r[1], Integer.parseInt(r[2]), null, false);
                    }
                })
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
