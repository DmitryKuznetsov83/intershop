package ru.yandex.practicum.intershop.service.user;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.AppUser;

public interface AppUserService {

    Mono<AppUser> addUser(AppUser user);

}
