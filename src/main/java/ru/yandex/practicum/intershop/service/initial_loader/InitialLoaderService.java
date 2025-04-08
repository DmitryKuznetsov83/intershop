package ru.yandex.practicum.intershop.service.initial_loader;

import reactor.core.publisher.Mono;

public interface InitialLoaderService {

    Mono<Void> load();

    Mono<Long> getItemCount();

}
