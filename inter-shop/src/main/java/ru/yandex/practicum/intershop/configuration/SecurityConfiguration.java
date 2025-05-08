package ru.yandex.practicum.intershop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import ru.yandex.practicum.intershop.repository.user.AppUserRepository;
import ru.yandex.practicum.intershop.service.user.AppUserServiceImpl;

import java.net.URI;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        var redirectServerAuthenticationSuccessHandler = new RedirectServerAuthenticationSuccessHandler("/main/items");

        var redirectServerLogoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        redirectServerLogoutSuccessHandler.setLogoutSuccessUrl(URI.create("/main/items"));

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                                .pathMatchers("/admin/**").hasRole("ADMIN")
                                .pathMatchers("/buy").access(withRoleHierarchy("ROLE_CLIENT"))
                                .pathMatchers("/cart/**").access(withRoleHierarchy("ROLE_CLIENT"))
                                .pathMatchers("/orders/**").access(withRoleHierarchy("ROLE_CLIENT"))
                                .pathMatchers(POST, "/items/**").access(withRoleHierarchy("ROLE_CLIENT"))
                                .pathMatchers(POST, "/main/**").access(withRoleHierarchy("ROLE_CLIENT"))
                                .pathMatchers(GET, "/images/**").permitAll()
                                .pathMatchers(GET, "/items/**").permitAll()
                                .pathMatchers(GET, "/main/**").permitAll()
                                .pathMatchers(GET, "/").permitAll()
                                .anyExchange().denyAll()
                )
                .formLogin(form -> form
                        .authenticationSuccessHandler(redirectServerAuthenticationSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler())
                )
                .build();
    }

    @Bean
    @Primary
    public ReactiveUserDetailsService userDetailsService(AppUserRepository appUserRepository) {
        return new AppUserServiceImpl(appUserRepository, passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private ReactiveAuthorizationManager<AuthorizationContext> withRoleHierarchy(String role) {
        return (authentication, context) -> authentication
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority ->
                                grantedAuthority.getAuthority().equals("ROLE_ADMIN") ||
                                        (role.equals("ROLE_CLIENT") && grantedAuthority.getAuthority().equals("ROLE_CLIENT"))
                        )
                )
                .map(AuthorizationDecision::new);
    }

}
