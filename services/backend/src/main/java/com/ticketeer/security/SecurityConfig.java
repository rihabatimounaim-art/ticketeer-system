package com.ticketeer.security;

import com.ticketeer.identity.infrastructure.JwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public JwtTokenValidator jwtTokenValidator(JwtProperties jwtProperties) {
        return new JwtTokenValidator(jwtProperties);
    }

    @Bean
    public JwtParser jwtParser(JwtProperties jwtProperties) {
        return new JwtParser(jwtProperties);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenValidator validator,
                                                           JwtParser jwtParser) {
        return new JwtAuthenticationFilter(validator, jwtParser);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/tickets/**").hasAnyRole("CUSTOMER", "ADMIN")
                        .requestMatchers("/control/**").hasAnyRole("AGENT", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
