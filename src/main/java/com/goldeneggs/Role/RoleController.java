package com.goldeneggs.Role;

import com.goldeneggs.Exception.DuplicateRoleNameException;
import com.goldeneggs.Exception.InvalidRoleDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Saves a new role in the system.
     *
     * @param role the role to be saved, should contain valid data and a unique name
     * @return a ResponseEntity containing:
     *         - the created Role with HTTP status 201 (CREATED) on success
     *         - an error message with HTTP status 400 (BAD REQUEST) if the provided role data is invalid
     *         - an error message with HTTP status 409 (CONFLICT) if the role name already exists
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Role role) {
        try {
            Role newRole = roleService.insert(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(newRole);
        } catch (InvalidRoleDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DuplicateRoleNameException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Retrieves all roles from the system.
     *
     * @return a ResponseEntity containing a list of all roles with HTTP status 200 (OK)
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Role>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id the ID of the role to retrieve
     * @return a ResponseEntity containing:
     *         - the found Role with HTTP status 200 (OK) on success
     *         - an error message with HTTP status 404 (NOT FOUND) if the role with the specified ID is not found
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(roleService.get(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Updates an existing role with the given data.
     *
     * @param id the ID of the role to be updated
     * @param role the updated role information
     * @return a ResponseEntity containing:
     *         - the updated Role with HTTP status 200 (OK) on success
     *         - an error message with HTTP status 404 (NOT FOUND) if the role with the specified ID is not found
     *         - an error message with HTTP status 400 (BAD REQUEST) if the provided role data is invalid
     *         - an error message with HTTP status 409 (CONFLICT) if the role name already exists
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Role role) {
        try {
            role.setId(id);
            roleService.update(role);
            return ResponseEntity.ok(role);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidRoleDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (DuplicateRoleNameException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id the ID of the role to delete
     * @return a ResponseEntity containing:
     *         - HTTP status 204 (NO CONTENT) on successful deletion
     *         - an error message with HTTP status 404 (NOT FOUND) if the role with the specified ID is not found
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            roleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
