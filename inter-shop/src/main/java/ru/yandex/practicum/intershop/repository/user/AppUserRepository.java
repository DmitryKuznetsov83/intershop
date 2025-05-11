package ru.yandex.practicum.intershop.repository.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.AppUser;

@Repository
public interface AppUserRepository extends ReactiveCrudRepository<AppUser, Long> {

    Mono<AppUser> findByLogin(String login);

}
