package com.goldeneggs.Users.Model.Rol;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goldeneggs.Users.Model.User.UsuarioRol;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long rolId;
    @Column(name = "rol_nombre", unique = true, nullable = false)
    private String rolNombre;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "rol")
    private Set<UsuarioRol> usuarioRoles = new HashSet<>();


}
