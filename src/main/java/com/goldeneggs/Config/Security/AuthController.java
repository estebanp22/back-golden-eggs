package com.goldeneggs.Config.Security;

import com.goldeneggs.Dto.AuthResponseDTO;
import com.goldeneggs.Dto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTGenerator jwtGenerator;

    /**
     * Authenticates the user and generates a JWT token.
     *
     * @param loginDto the login credentials of the user (username and password).
     * @return a response containing the generated JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); // Para ver en consola exactamente qué está fallando
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Credenciales inválidas"));
        }
    }

    /**
     * Logs out the user by invalidating the session.
     *
     * (This can be client-side handled, but can be included here if you want to invalidate tokens server-side.)
     *
     * @return a response confirming that the user has been logged out.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        SecurityContextHolder.clearContext();

        Map<String, String> response = new HashMap<>();
        response.put("message", "User logged out successfully.");

        return ResponseEntity.ok(response);
    }
}
