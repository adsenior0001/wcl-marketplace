package com.wcl.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // <--- Added this to ensure Spring always detects it
public class SecurityConfig {

    // Keep your password encoder to hash passwords securely!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. OPEN DOORS: Allow anyone to access these two endpoints
                        .requestMatchers("/api/v1/users/register", "/api/v1/users/login").permitAll()
                        
                        // 2. LOCKED DOORS: Require a valid JWT token for absolutely everything else
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}