package com.goldeneggs.Users.Model.Rol;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goldeneggs.Users.Model.User.UserRole;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "role")
    private Set<UserRole> usersRole = new HashSet<>();


}
