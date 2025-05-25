package com.goldeneggs.Config.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(jwtAuthEntryPoint)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // === SWAGGER ===
                                .requestMatchers(
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()

                                // === AUTH ===
                                .requestMatchers("/api/auth/**").permitAll()

                                // === ENDPOINTS PÚBLICOS ===
                                .requestMatchers("/api/v1/visits").permitAll()
                                .requestMatchers("/api/v1/eggs/getAll").permitAll()
                                .requestMatchers("/api/v1/eggs/getAllEggDto").permitAll()
                                .requestMatchers("api/v1/users/register").permitAll()
                                .requestMatchers("/api/v1/orders/save").permitAll()

                                // === ENDPOINTS PROTEGIDOS ===
                                .requestMatchers("/api/v1/visits/count").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/bills/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/eggs/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/inventories/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/orders/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/payments/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/reports/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/roles/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/suppliers/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/egg-types/**").hasAuthority("ADMIN")
                                .requestMatchers("/api/v1/users/getByUsername/**").authenticated()
                                .requestMatchers("/api/v1/users/update/**").authenticated()
                                .requestMatchers("/api/v1/users/updatepass/password/**").authenticated()
                                .requestMatchers("/api/v1/users/**").hasAuthority("ADMIN")

                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost/", "http://localhost:8080", "http://localhost:80", "http://localhost", "http://localhost:6010"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

}
