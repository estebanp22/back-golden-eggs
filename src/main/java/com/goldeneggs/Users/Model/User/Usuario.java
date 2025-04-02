package com.goldeneggs.Users.Model.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "administradores")
public class Usuario implements  UserDetails {

    @Id
    @Column(name = "documento", unique = true, nullable = false)
    private Long documento;
    @Column(name = "correo", unique = true, nullable = false)
    private String email;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "pass", nullable = false)
    private String password;
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
    @Column(name = "username", unique = true, nullable = false)
    private String username;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "usuario")
    @JsonIgnore
    private Set<UsuarioRol> usuarioRoles = new HashSet<>();

    public Usuario() {
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (UsuarioRol usuarioRol : this.usuarioRoles) {
            authorities.add(new SimpleGrantedAuthority(usuarioRol.getRol().getRolNombre()));
        }
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public String getPassword(){
        return this.password;
    }

    @Override
    public String getUsername(){
        return this.username;
    }
}


