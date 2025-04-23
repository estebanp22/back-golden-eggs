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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

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
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate the JWT token
        String token = jwtGenerator.generateToken(authentication);

        // Return the token in the response
        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
    }

    /**
     * Logs out the user by invalidating the session.
     *
     * (This can be client-side handled, but can be included here if you want to invalidate tokens server-side.)
     *
     * @return a response confirming that the user has been logged out.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Invalidate the authentication context on the server (not necessary with stateless JWT, but could be useful in some cases)
        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.OK).body("User logged out successfully.");
    }
}
