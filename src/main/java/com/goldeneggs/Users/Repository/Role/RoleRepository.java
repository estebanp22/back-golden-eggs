package com.goldeneggs.Users.Repository.Role;


import com.goldeneggs.Users.Model.Role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
