package ru.javaops.cloudjava.menuservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers(HttpMethod.GET, "/v1/menu-items/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/menu-items/menu-info").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/menu-items/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/menu-items/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/v1/menu-items/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
                .sessionManagement(customizer -> customizer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        var authConverter = new JwtAuthenticationConverter();
        authConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authConverter;
    }
}