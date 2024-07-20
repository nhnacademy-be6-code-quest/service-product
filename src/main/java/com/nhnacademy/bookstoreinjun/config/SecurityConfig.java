package com.nhnacademy.bookstoreinjun.config;

import com.nhnacademy.bookstoreinjun.filter.HeaderFilter;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final URI productClientPath = URI.create("/api/product/client/**");
    private final URI productAdminPath = URI.create("/api/product/admin/**");
    private static final String  ADMIN = "ROLE_ADMIN";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new HeaderFilter(List.of(
                        new HeaderFilter.RouteConfig(productClientPath, HttpMethod.GET.name(), Collections.emptyList()),
                        new HeaderFilter.RouteConfig(productClientPath, HttpMethod.POST.name(), Collections.emptyList()),
                        new HeaderFilter.RouteConfig(productClientPath, HttpMethod.PUT.name(), Collections.emptyList()),
                        new HeaderFilter.RouteConfig(productClientPath, HttpMethod.DELETE.name(), Collections.emptyList()),
                        new HeaderFilter.RouteConfig(productAdminPath, HttpMethod.GET.name(), List.of(ADMIN)),
                        new HeaderFilter.RouteConfig(productAdminPath, HttpMethod.POST.name(), List.of(ADMIN)),
                        new HeaderFilter.RouteConfig(productAdminPath, HttpMethod.PUT.name(), List.of(ADMIN))
                )), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req ->
                                req.anyRequest().permitAll()
                )
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
