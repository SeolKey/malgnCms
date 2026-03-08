package com.malgn.config.security;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class ActuatorSecurityConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) {
        http.securityMatcher(EndpointRequest.toAnyEndpoint());

        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());

        return http.build();
    }

}
