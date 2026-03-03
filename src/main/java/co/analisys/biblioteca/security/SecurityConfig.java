package co.analisys.biblioteca.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;

    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger/OpenAPI - acceso publico
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()
                        // H2 Console - acceso publico (desarrollo)
                        .requestMatchers("/h2-console/**").permitAll()

                        // GET prestamos - ADMIN, BIBLIOTECARIO y USUARIO pueden consultar
                        .requestMatchers(HttpMethod.GET, "/circulacion/**")
                        .hasAnyRole("ADMIN", "BIBLIOTECARIO", "USUARIO")

                        // POST circulacion (prestar/devolver) - ADMIN y BIBLIOTECARIO
                        .requestMatchers(HttpMethod.POST, "/circulacion/**").hasAnyRole("ADMIN", "BIBLIOTECARIO")

                        // PUT circulacion - ADMIN y BIBLIOTECARIO
                        .requestMatchers(HttpMethod.PUT, "/circulacion/**").hasAnyRole("ADMIN", "BIBLIOTECARIO")

                        // DELETE circulacion - solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/circulacion/**").hasRole("ADMIN")

                        // Cualquier otra peticion requiere autenticacion
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Necesario para H2 Console (iframes)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
