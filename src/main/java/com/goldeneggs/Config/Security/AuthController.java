package com.goldeneggs.Config.Security;

import com.goldeneggs.Dto.AuthResponseDTO;
import com.goldeneggs.Dto.LoginDto;
import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Role.RoleRepository;
import com.goldeneggs.User.User;
import com.goldeneggs.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTGenerator jwtGenerator;


    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsById(registerDto.getId())) {
            return new ResponseEntity<>("Id is taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));
        user.setId(registerDto.getId());
        user.setAddress(registerDto.getAddress());
        user.setEmail(registerDto.getEmail());
        user.setEnabled(true);
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setName(registerDto.getName());


        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));


        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }


    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
    }
}
