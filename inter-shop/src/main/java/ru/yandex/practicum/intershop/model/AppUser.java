package ru.yandex.practicum.intershop.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.practicum.intershop.emun.AppUserRole;

@Table(name="app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    private Long id;

    String login;

    String password;

    AppUserRole role;

}
