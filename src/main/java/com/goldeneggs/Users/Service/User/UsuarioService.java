package com.goldeneggs.Users.Service.User;


import com.goldeneggs.Users.Model.Rol.Rol;
import com.goldeneggs.Users.Model.User.Usuario;
import com.goldeneggs.Users.Model.User.UsuarioRol;

import java.util.List;
import java.util.Set;

public interface UsuarioService {


    public Usuario guardarUsuario(Usuario usuario, Set<UsuarioRol> usuarioRoles) throws Exception;

    public void save(Usuario usuario) throws Exception;

    public Usuario obtenerUsuario(String username);

    public Usuario obtenerUsuarioEmail(String email);

    public List<Usuario> getAllUsers();

    public void eliminarUsuario(Long usuarioId);

    public Usuario actualizarUsuario(String username, Usuario nuevoUsuario, Rol rol);

    public Usuario deshabilitarUsuario(Usuario usuario);

    public Usuario updatePassword(Usuario usuario, String emailRequest)
            //throws org.springframework.messaging.MessagingException, MessagingException
            ;

    public Usuario activarUsuario(Usuario usuario);

    public Usuario updatePasswordModule(Usuario usuario, String newPassword);

}




