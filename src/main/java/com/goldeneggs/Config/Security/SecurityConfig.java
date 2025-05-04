package com.goldeneggs.Config.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Usa el CorsConfigurationSource que definimos abajo
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(jwtAuthEntryPoint)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // Aquí tu frontend
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Necesario si usas JWT en cookies o cualquier autenticación con credenciales

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

    /*

    EJEMPLO DEL MANEJO DE AUTORIZACION DE ROLES A ENDPOINTS

    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors() // Habilita CORS
                .and()
                .csrf().disable() // Deshabilitar CSRF
                .authorizeRequests()
                .antMatchers("/api/v1/user/updatePassword/**").permitAll()
                .antMatchers("/generate-token").permitAll()
                .antMatchers("/api/v1/user/save/**").hasAnyAuthority("Desarrollador")
                .antMatchers("/api/v1/user/get/**").hasAnyAuthority("Desarrollador", "Administrador", "Director Técnico", "Gestor de Calidad","SST", "Inspector de Línea", "Gestor Documental")
                .antMatchers("/api/v1/user/updatePasswordModule/**").hasAnyAuthority("Desarrollador", "Administrador", "Director Técnico", "Gestor de Calidad","SST", "Inspector de Línea", "Gestor Documental")
                .antMatchers("/api/v1/user/getAll/**").hasAnyAuthority("Desarrollador")
                .antMatchers("/api/v1/user/active/**").hasAnyAuthority("Desarrollador")
                .antMatchers("/api/v1/user/disable/**").hasAnyAuthority("Desarrollador")
                .antMatchers("/api/v1/user/update/**").hasAnyAuthority("Desarrollador")

                .antMatchers("/api/v1/referidos/**").hasAnyAuthority("Desarrollador", "Administrador")
                .antMatchers("/api/v1/referidos-pagos/**").hasAnyAuthority("Desarrollador", "Administrador")


                .antMatchers("/api/v1/control-registros/**").hasAnyAuthority("Desarrollador", "Administrador", "SST")
                .antMatchers("/api/v1/anotaciones/**").hasAnyAuthority("Desarrollador", "Administrador", "SST")
                .antMatchers("/api/v1/permiso/**").hasAnyAuthority("Desarrollador", "Administrador", "SST")
                .antMatchers("/api/v1/articulos/**").hasAnyAuthority("Desarrollador", "Administrador")
                .antMatchers("/api/v1/inventario/**").hasAnyAuthority("Desarrollador", "Administrador")
                .antMatchers("/api/v1/articulos-funcionarios/**").hasAnyAuthority("Desarrollador", "Administrador")
                .antMatchers("/api/v1/supervision/**").hasAnyAuthority("Desarrollador", "Administrador", "Director Técnico", "Gestor de Calidad")
                .antMatchers("/api/v1/progreso-supervision/**").hasAnyAuthority("Desarrollador", "Administrador", "Director Técnico", "Gestor de Calidad")


                .antMatchers("/api/v1/rol/**").hasAnyAuthority("Desarrollador")
                .antMatchers("/api/v1/vacaciones/**").hasAnyAuthority("Desarrollador", "Administrador","SST")
                .antMatchers("/api/v1/historial/**").hasAnyAuthority("Desarrollador", "Administrador")
                .antMatchers("/api/v1/bitacoras/**").hasAnyAuthority("Desarrollador", "Administrador","Director Técnico", "Gestor de Calidad", "Inspector de Línea")
                .antMatchers("/api/v1/bitacora-revisada/**").hasAnyAuthority("Desarrollador", "Administrador","Director Técnico", "Gestor de Calidad")
                .antMatchers("/api/v1/equipo-bitacora/**").hasAnyAuthority("Desarrollador", "Administrador","Director Técnico", "Gestor de Calidad", "Inspector de Línea")
                .antMatchers("/api/v1/encuesta/**").hasAnyAuthority("Desarrollador", "Administrador")

                .antMatchers(HttpMethod.OPTIONS).permitAll() // Permitir opciones sin autenticación
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Sin estado (JWT)

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

     */


}
