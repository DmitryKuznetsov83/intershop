package ru.yandex.practicum.intershop.controller.advice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.yandex.practicum.intershop.dto.UserDto;
import ru.yandex.practicum.intershop.service.user.AppUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("user")
    public UserDto addCurrentUser(@AuthenticationPrincipal AppUserDetails userDetails) {
        return new UserDto(userDetails);
    }

}
