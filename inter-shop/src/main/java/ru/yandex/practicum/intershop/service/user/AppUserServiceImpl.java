package ru.yandex.practicum.intershop.service.user;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.model.AppUser;
import ru.yandex.practicum.intershop.repository.user.AppUserRepository;

import static ru.yandex.practicum.intershop.emun.AppUserRole.ROLE_ADMIN;
import static ru.yandex.practicum.intershop.emun.AppUserRole.ROLE_CLIENT;

@Service
public class AppUserServiceImpl implements AppUserService, ReactiveUserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<AppUser> addUser(AppUser user) {
        return appUserRepository.save(user);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void addAppUsers() {
        AppUser admin = AppUser.builder()
                .login("Admin")
                .password(passwordEncoder.encode("Admin"))
                .role(ROLE_ADMIN)
                .build();

        AppUser client1 = AppUser.builder()
                .login("Client_1")
                .password(passwordEncoder.encode("Client_1"))
                .role(ROLE_CLIENT)
                .build();

        AppUser client2 = AppUser.builder()
                .login("Client_2")
                .password(passwordEncoder.encode("Client_2"))
                .role(ROLE_CLIENT)
                .build();

        appUserRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> Flux.concat(
                        appUserRepository.save(admin),
                        appUserRepository.save(client1),
                        appUserRepository.save(client2)
                ))
                .then()
                .onErrorResume(e -> {
                    return Mono.empty();
                }).subscribe();

    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return appUserRepository.findByLogin(username)
                .map(appUser -> {
                    String[] roles = appUser.getRole() != null ? new String[]{appUser.getRole().name()} : new String[0];
                    return (UserDetails) AppUserDetails.builder()
                            .id(appUser.getId())
                            .login(appUser.getLogin())
                            .password(appUser.getPassword())
                            .role(appUser.getRole())
                            .build();
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Пользователь с логином " + username + " не найден")));
    }
}
