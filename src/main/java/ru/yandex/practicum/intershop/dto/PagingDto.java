package ru.yandex.practicum.intershop.dto;

public record PagingDto(Integer pageSize,
                        Integer pageNumber,
                        boolean hasPrevious,
                        boolean hasNext) {}