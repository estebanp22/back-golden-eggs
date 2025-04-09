package com.goldeneggs.Users.Service.User;


import com.goldeneggs.Users.Model.Rol.Role;
import com.goldeneggs.Users.Model.User.User;
import com.goldeneggs.Users.Model.User.UserRole;
import com.goldeneggs.Users.Repository.Rol.RoleRepository;
import com.goldeneggs.Users.Repository.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user, Set<UserRole> userRoles) throws Exception {
        User userLocal = userRepository.findByUsername(user.getUsername());
        if (userLocal != null) {
            throw new Exception("The user is already present");
        } else {
            for (UserRole userRole : userRoles) {
                roleRepository.save(userRole.getRole());
            }
            user.getUserRoles().addAll(userRoles);
            userLocal = userRepository.save(user);
        }
        return userLocal;
    }

    @Override
    public void save(User user) throws Exception {
        userRepository.save(user);
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        userRepository.deleteById(userId);
    }

    @Override
    public User updateUser(String username, User newUser, Role role) {
        User user = userRepository.findByUsername(username);

        UserRole existingUserRole = user.getUserRoles().iterator().next();

        existingUserRole.setRole(role);

        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User disableUser(User user) {
        user.setEnabled(false);

        return userRepository.save(user);
    }

    @Override
    public User updatePassword(User user, String requestedEmail)
    //throws MessagingException, javax.mail.MessagingException
    {
        // Validate if the email doesn't match and throw exception

        /*
        if (!Objects.equals(requestedEmail, user.getEmail())) {
            throw new IllegalArgumentException("The provided email does not match the user's email.");
        }

        // Generate and encrypt new password
        String newPassword = this.generateRandomString();
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);

        // Build email message
        String subject = "Password Recovery";
        String emailBody = new StringBuilder()
                .append("Estimado(a) ").append(usuario.getNombre()).append(",\n\n")
                .append("Se ha realizado un cambio de contraseña en su cuenta de Golden Eggs.\n\n")
                .append("Si usted no ha solicitado este cambio, por favor contacte con el administrador del sistema: ")
                .append("d.co\n\n")
                .append("Gracias por utilizar nuestros servicios. Su nueva contraseña temporal es: ")
                .append(nuevaContrasena)
                .toString();

        // Send email
        emailService.sendHtmlEmail(user.getEmail(), subject, emailBody);

        // Save the user with the new password
        userRepository.save(user);

        return user;

         */
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    private String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int minLength = 8;
        SecureRandom random = new SecureRandom();
        StringBuilder randomString = new StringBuilder(minLength);

        for (int i = 0; i < minLength; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
    }

    @Override
    public User activateUser(User user) {
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Override
    public User updatePasswordModule(User user, String newPassword) {
        // Validate that the user exists
        User oldUser = this.getUser(user.getUsername());
        if (oldUser == null) {
            //throw new ResourceNotFoundException("Usuario no encontrado con el username: " + user.getUsername());
        }

        // Encrypt the new password
        String encryptedPassword = passwordEncoder.encode(newPassword);
        assert oldUser != null;
        oldUser.setPassword(encryptedPassword);

        // Save the updated user in the database
        return userRepository.save(oldUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
