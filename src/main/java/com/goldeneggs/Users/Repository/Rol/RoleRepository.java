package com.goldeneggs.Users.Repository.Rol;


import com.goldeneggs.Users.Model.Rol.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
