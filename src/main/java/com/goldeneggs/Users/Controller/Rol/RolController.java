package com.goldeneggs.Users.Controller.Rol;

import com.goldeneggs.Users.Model.Rol.Rol;
import com.goldeneggs.Users.Service.Rol.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/rol")
public class RolController {

    @Autowired
    RolService rolService;


    /**
     *
     * @param rol
     *
     * {
     *     "rolNombre":"Gestor Documental"
     * }
     *
     *
     * @return
     */
    @PostMapping("/save")
    public ResponseEntity<Rol> save(@RequestBody Rol rol) {
        try{
            Rol newRol = rolService.insert(rol);
            return ResponseEntity.ok(newRol);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }




    @GetMapping("/getAll")
    public ResponseEntity<List<Rol>> getAll() {
        try{
            List<Rol>roles = rolService.getAll();
            return ResponseEntity.ok(roles);

        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

}
