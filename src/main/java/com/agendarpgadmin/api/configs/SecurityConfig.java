package com.agendarpgadmin.api.configs;

import com.agendarpgadmin.api.filters.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        log.info(Arrays.toString(frontendBaseUrl.split(",")));
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
                Arrays.stream(frontendBaseUrl.split(","))
                        .map(String::trim)
                        .toList()
        );

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PATCH",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilita CORS
                .csrf(csrf -> {
                    csrf.disable();
                    log.info("CSRF desabilitado");
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/events/my-created").authenticated()
                        .requestMatchers(HttpMethod.GET, "/events").permitAll()
                        .requestMatchers(HttpMethod.GET, "/events/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/events/*/activities").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}