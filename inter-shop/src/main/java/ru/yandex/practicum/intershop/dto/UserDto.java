package ru.yandex.practicum.intershop.dto;

import lombok.*;
import ru.yandex.practicum.intershop.emun.AppUserRole;
import ru.yandex.practicum.intershop.service.user.AppUserDetails;

@Data
@AllArgsConstructor
@ToString
public class UserDto {

    private String login;
    private boolean isAuthorized;
    private AppUserRole role;

    public UserDto(AppUserDetails userDetails) {
        if (userDetails == null) {
            this.login = "Anonymous";
            this.isAuthorized = false;
            this.role = null;
        } else {
            this.login = userDetails.getUsername();
            this.isAuthorized = true;
            this.role = userDetails.getRole();
        }
    }

}
