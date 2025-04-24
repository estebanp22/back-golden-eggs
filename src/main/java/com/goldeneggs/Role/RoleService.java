package com.goldeneggs.Role;

import com.goldeneggs.Exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service interface for managing roles.
 */
public interface RoleService {

    /**
     * Retrieves a role by its ID.
     *
     * @param id the ID of the role
     * @return the found Role, or null if not found
     */
    Role get(Long id);

    /**
     * Retrieves all roles from the system.
     *
     * @return a list of all roles
     */
    List<Role> getAll();

    /**
     * Inserts a new role into the system.
     *
     * @param role the role to be inserted
     * @return the inserted role
     */
    Role insert(Role role);

    /**
     * Updates an existing role.
     *
     * @param role the role with updated information
     */
    void update(Role role);

    /**
     * Deletes a role by its ID.
     *
     * @param id the ID of the role to delete
     * @throws ResourceNotFoundException if the role with the given ID is not found
     */
    void delete(Long id);

}
