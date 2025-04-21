package com.goldeneggs.Users.Controller.Role;

import com.goldeneggs.Users.Model.Role.Role;
import com.goldeneggs.Users.Service.Role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/role")
public class RolController {

    @Autowired
    RoleService roleService;


    /**
     *
     * @param role
     *
     * {
     *     "rolName":"Empleado"
     * }
     *
     *
     * @return Ok or BadRequest
     */
    @PostMapping("/save")
    public ResponseEntity<Role> save(@RequestBody Role role) {
        try{
            Role newRole = roleService.insert(role);
            return ResponseEntity.ok(newRole);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }




    @GetMapping("/getAll")
    public ResponseEntity<List<Role>> getAll() {
        try{
            List<Role>roles = roleService.getAll();
            return ResponseEntity.ok(roles);

        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

}
