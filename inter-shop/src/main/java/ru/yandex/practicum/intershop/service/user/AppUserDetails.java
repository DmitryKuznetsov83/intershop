package ru.yandex.practicum.intershop.service.user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.yandex.practicum.intershop.emun.AppUserRole;

import java.util.Collection;
import java.util.List;

@Builder
@Getter
public class AppUserDetails implements UserDetails {

    private final Long id;
    private final String login;
    private final String password;
    private final AppUserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

}
