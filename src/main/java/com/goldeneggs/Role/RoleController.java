package com.goldeneggs.Role;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Saves a new role in the system.
     *
     * @param role Role data to be saved.
     * @return ResponseEntity with the saved role or a BadRequest if something goes wrong.
     */
    @PostMapping("/save")
    public ResponseEntity<Role> save(@RequestBody Role role) {
        try {
            Role newRole = roleService.insert(role);
            return ResponseEntity.ok(newRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Retrieves all roles from the system.
     *
     * @return ResponseEntity with a list of all roles.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Role>> getAll() {
        try {
            List<Role> roles = roleService.getAll();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id ID of the role to retrieve.
     * @return ResponseEntity with the role if found, otherwise a NotFound status.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Role> getById(@PathVariable Long id) {
        try {
            Role role = roleService.get(id);
            return ResponseEntity.ok(role);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates an existing role.
     *
     * @param id   ID of the role to update.
     * @param role Updated role data.
     * @return ResponseEntity with the updated role or NotFound status if the role does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Role> update(@PathVariable Long id, @RequestBody Role role) {
        try {
            role.setId(id);  // Ensure the ID is updated correctly
            roleService.update(role);
            return ResponseEntity.ok(role);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Deletes a role by its ID.
     *
     * @param id ID of the role to delete.
     * @return ResponseEntity with no content if deleted or NotFound status if the role does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            roleService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
