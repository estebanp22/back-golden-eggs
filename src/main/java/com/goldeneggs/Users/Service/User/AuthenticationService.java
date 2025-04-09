package com.goldeneggs.Users.Service.User;

import com.goldeneggs.Users.Model.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Obtiene el usuario autenticado desde el contexto de seguridad.
     * @return Usuario autenticado.
     */
    public User getCurrentUser() {
        // Obtener el nombre de usuario desde el contexto de seguridad
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No hay un usuario autenticado");
        }

        String username = authentication.getName();

        // Usar el UserDetailsServiceImpl para cargar los detalles del usuario
        return (User) userDetailsService.loadUserByUsername(username);
    }
}
