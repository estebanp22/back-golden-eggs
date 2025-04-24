package com.goldeneggs.Config.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Entry point for unauthorized requests.
 * This is triggered when a request to a protected resource is made without proper authentication.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Responds with a 401 Unauthorized status when authentication fails.
     *
     * @param request       the HTTP request
     * @param response      the HTTP response
     * @param authException the authentication exception thrown
     * @throws IOException      in case of I/O errors
     * @throws ServletException in case of servlet errors
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized access - " + authException.getMessage() + "\"}");
    }
}