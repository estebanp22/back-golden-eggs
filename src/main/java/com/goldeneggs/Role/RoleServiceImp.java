package com.goldeneggs.Role;

import com.goldeneggs.Exception.DuplicateRoleNameException;
import com.goldeneggs.Exception.InvalidRoleDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the RoleService interface.
 */
@Service
public class RoleServiceImp implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Retrieves a role by its ID.
     *
     * @param id the ID of the role
     * @return the found Role
     * @throws ResourceNotFoundException if the role with the given ID is not found
     */
    @Override
    public Role get(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + id));
    }

    /**
     * Retrieves all roles from the system.
     *
     * @return a list of all roles
     */
    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    /**
     * Inserts a new role into the system.
     *
     * @param role the role to be inserted
     * @return the inserted role
     * @throws InvalidRoleDataException if the role data is invalid
     * @throws DuplicateRoleNameException if a role with the same name already exists
     */
    @Override
    public Role insert(Role role) {
        if (!RoleValidator.isValidName(role.getName())) {
            throw new InvalidRoleDataException("Role name is invalid");
        }
        if (roleRepository.findByNameIgnoreCase(role.getName()).isPresent()) {
            throw new DuplicateRoleNameException("Role name already exists: " + role.getName());
        }
        return roleRepository.save(role);
    }

    /**
     * Updates an existing role.
     *
     * @param role the role with updated information
     * @throws ResourceNotFoundException if the role to update does not exist
     * @throws InvalidRoleDataException if the updated data is invalid
     * @throws DuplicateRoleNameException if the new name conflicts with an existing role
     */
    @Override
    public void update(Role role) {
        Role existingRole = roleRepository.findById(role.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id " + role.getId()));

        if (!RoleValidator.isValidName(role.getName())) {
            throw new InvalidRoleDataException("Role name is invalid");
        }

        Optional<Role> roleByName = roleRepository.findByNameIgnoreCase(role.getName());
        if (roleByName.isPresent() && !roleByName.get().getId().equals(role.getId())) {
            throw new DuplicateRoleNameException("Another role already exists with name: " + role.getName());
        }

        existingRole.setName(role.getName());
        roleRepository.save(existingRole);
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id the ID of the role to delete
     * @throws ResourceNotFoundException if the role with the given ID is not found
     */
    @Override
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id " + id);
        }
        roleRepository.deleteById(id);
    }
}
