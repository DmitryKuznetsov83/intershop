package ru.yandex.practicum.intershop.dto;

import java.util.List;

public record PageDto<T>(List<T> content,
                         int totalPages,
                         long totalElements,
                         boolean hasNext,
                         boolean hasPrevious) {
}
