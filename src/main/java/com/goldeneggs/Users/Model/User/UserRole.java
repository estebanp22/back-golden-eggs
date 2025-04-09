package com.goldeneggs.Users.Model.User;


import com.goldeneggs.Users.Model.Rol.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    @ManyToOne
    private Role role;

}
