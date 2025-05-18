package com.goldeneggs.Role;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    /**
     * Finds a role based on its name.
     *
     * @param name the name of the role to search for, must not be null or empty
     * @return an Optional containing the Role if found, or an empty Optional if no role exists with the given name
     */
    Optional<Role> findByName(String name);

    /**
     * Finds a role based on its name, ignoring case.
     *
     * @param name the name of the role to search for, must not be null or empty
     * @return an Optional containing the Role if found, or an empty Optional if no role exists with the given name
     */
    Optional<Role> findByNameIgnoreCase(String name);

}
