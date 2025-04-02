package com.goldeneggs.Users.Service.User;


import com.goldeneggs.Users.Model.Rol.Rol;
import com.goldeneggs.Users.Model.User.Usuario;
import com.goldeneggs.Users.Model.User.UsuarioRol;
import com.goldeneggs.Users.Repository.Rol.RolRepository;
import com.goldeneggs.Users.Repository.User.UsuarioRepository;
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
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    //@Autowired
    private PasswordEncoder passwordEncoder;



    @Override
    public Usuario guardarUsuario(Usuario usuario, Set<UsuarioRol> usuarioRoles) throws Exception {
        Usuario usuarioLocal = usuarioRepository.findByUsername(usuario.getUsername());
        if (usuarioLocal != null) {
            throw new Exception("El usuario ya está presente");
        } else {
            for (UsuarioRol usuarioRol : usuarioRoles) {
                rolRepository.save(usuarioRol.getRol());
            }
            usuario.getUsuarioRoles().addAll(usuarioRoles);
            usuarioLocal = usuarioRepository.save(usuario);

        }
        return usuarioLocal;
    }

    @Override
    public void save(Usuario usuario) throws Exception {
        usuarioRepository.save(usuario);
    }

    @Override
    public Usuario obtenerUsuario(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public void eliminarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        usuarioRepository.deleteById(usuarioId);

    }

    @Override
    public Usuario actualizarUsuario(String username, Usuario nuevoUsuario, Rol rol) {
        Usuario usuario = usuarioRepository.findByUsername(username);

        UsuarioRol usuarioRolExistente = usuario.getUsuarioRoles().iterator().next();

        usuarioRolExistente.setRol(rol);

        usuario.setNombre(nuevoUsuario.getNombre());
        usuario.setEmail(nuevoUsuario.getEmail());
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return updatedUsuario;
    }


    @Override
    public Usuario deshabilitarUsuario(Usuario usuario) {
        usuario.setEnabled(false);
        Usuario updatedUsuario = usuarioRepository.save(usuario);

        return updatedUsuario;
    }


    @Override
    public Usuario updatePassword(Usuario usuario, String emailSolicitado)
            //throws MessagingException, javax.mail.MessagingException
    {
        // Validar si el email no coincide y lanzar excepción

        /*
        if (!Objects.equals(emailSolicitado, usuario.getEmail())) {
            throw new IllegalArgumentException("El email proporcionado no coincide con el del usuario.");
        }

        // Generar y encriptar nueva contraseña
        String nuevaContrasena = this.generateRandomString();
        String contrasenaEncriptada = passwordEncoder.encode(nuevaContrasena);
        usuario.setPassword(contrasenaEncriptada);

        // Construir mensaje de correo
        String asunto = "Recuperación de contraseña";
        String cuerpoCorreo = new StringBuilder()
                .append("Estimado(a) ").append(usuario.getNombre()).append(",\n\n")
                .append("Se ha realizado un cambio de contraseña en su cuenta de Cenda Gestión.\n\n")
                .append("Si usted no ha solicitado este cambio, por favor contacte con el administrador del sistema: ")
                .append("desarrollo_software@cenda.com.co\n\n")
                .append("Gracias por utilizar nuestros servicios. Su nueva contraseña temporal es: ")
                .append(nuevaContrasena)
                .toString();

        // Enviar correo
        emailService.sendHtmlEmail(usuario.getEmail(), asunto, cuerpoCorreo);

        // Guardar el usuario con la nueva contraseña
        usuarioRepository.save(usuario);

        return usuario;

         */
        return null;
    }



    @Override
    public Usuario obtenerUsuarioEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
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
    public Usuario activarUsuario(Usuario usuario) {
        usuario.setEnabled(true);
        Usuario updatedUsuario = usuarioRepository.save(usuario);

        return updatedUsuario;
    }

    @Override
    public Usuario updatePasswordModule(Usuario usuario, String nuevaContrasena) {
        // Validar que el usuario existe
        Usuario oldUsuario = this.obtenerUsuario(usuario.getUsername());
        if (oldUsuario == null) {
            //throw new ResourceNotFoundException("Usuario no encontrado con el username: " + usuario.getUsername());
        }

        // Encriptar la nueva contraseña
        String passwordEncriptada = passwordEncoder.encode(nuevaContrasena);
        oldUsuario.setPassword(passwordEncriptada);

        // Guardar el usuario actualizado en la base de datos
        return usuarioRepository.save(oldUsuario);
    }



    @Override
    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }
}
