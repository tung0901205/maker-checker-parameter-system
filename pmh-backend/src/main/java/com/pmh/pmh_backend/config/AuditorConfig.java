package com.pmh.pmh_backend.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class AuditorConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                return Optional.ofNullable((String) principal);
            } catch (Exception e) {
                return Optional.of("system");
            }
        };
    }
}