package com.goldeneggs.Users.Repository.User;

import com.goldeneggs.Users.Model.User.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    public Usuario findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

}
