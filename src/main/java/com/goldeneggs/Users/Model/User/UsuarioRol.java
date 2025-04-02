package com.goldeneggs.Users.Model.User;


import com.goldeneggs.Users.Model.Rol.Rol;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioRolId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_documento", referencedColumnName = "documento", nullable = false)
    private Usuario usuario;


    @ManyToOne
    private Rol rol;

}
