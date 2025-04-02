package com.goldeneggs.Users.Controller.User;

import com.goldeneggs.Users.Model.Rol.Rol;
import com.goldeneggs.Users.Model.User.Usuario;
import com.goldeneggs.Users.Model.User.UsuarioRol;
import com.goldeneggs.Users.Service.Rol.RolService;
import com.goldeneggs.Users.Service.User.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    //@Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * @param usuario{ "documento":"14703892",
     *                 "email":"izquierdin666@hotmail.com",
     *                 "nombre":"Andrés Izquierdo",
     *                 "enabled":1
     *                 }
     * @param rol_id   /api/v1/user/save/2
     *                 <p>
     *                 Guarda a un nuevo administrador asignandole el rol
     *                 recordar que username siempre es el mismo número de documento, así mismo la contraseña, ya que el usuario debe cambiarla en el login
     * @return
     */
    @PostMapping("/save/{rol_id}")
    public ResponseEntity<Usuario> save(@RequestBody Usuario usuario, @PathVariable Long rol_id) {
        try {
            Rol rol = rolService.get(rol_id);
            if (rol == null) {
                return ResponseEntity.notFound().build();
            } else {
                String passEncriptada = passwordEncoder.encode(usuario.getDocumento().toString());
                usuario.setPassword(passEncriptada);
                usuario.setUsername(String.valueOf(usuario.getDocumento()));
                usuario.setEnabled(true);
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setRol(rol);
                usuarioRol.setUsuario(usuario);
                Set<UsuarioRol> usuariosRoles = Collections.singleton(usuarioRol);
                Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario, usuariosRoles);
                return ResponseEntity.ok(usuarioGuardado);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get/{username}")
    public ResponseEntity<Usuario> get(@PathVariable String username) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(username);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Usuario>> getAll() {
        try {
            List<Usuario> users = usuarioService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * @param username Activa a un administrador cambiando el atributo enabled a true permitiéndole iniciar sesión de nuevo
     * @return
     */

    @PutMapping("/active/{username}")
    public ResponseEntity<Usuario> activeUser(@PathVariable String username) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(username);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            usuarioService.activarUsuario(usuario);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @param username
     * @return Desactiva a un administrador cambiando el atributo enabled a false, esto impide que el administrador pueda iniciar sesión.
     */
    @PutMapping("/disable/{username}")
    public ResponseEntity<Usuario> delete(@PathVariable String username) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(username);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }
            usuarioService.deshabilitarUsuario(usuario);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updatePassword/{username}")
    public ResponseEntity<?> updatePassword(@PathVariable String username, @RequestBody Map<String, String> request) {
        try {
            Usuario usuario = usuarioService.obtenerUsuario(username);
            if (usuario == null) {
                return ResponseEntity.notFound().build();
            }

            String email = request.get("email");
            Usuario nuevoUsuario = usuarioService.updatePassword(usuario, email);

            return ResponseEntity.ok(nuevoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Deja un log de la excepción para saber qué pasó
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado.");
        }
    }



    /**
     * Endpoint para actualizar la contraseña de un usuario.
     * Este endpoint es accesible desde el módulo de login cuando el usuario solicita la recuperación de su contraseña.
     *
     * @param username El nombre de usuario cuyo password será actualizado.
     * @param requestBody La nueva contraseña enviada en el cuerpo de la solicitud.
     * @return ResponseEntity con el estado de la operación y, si es exitoso, el usuario actualizado.
     */
    @PutMapping("/updatePasswordModule/{username}")
    public ResponseEntity<?> updatePasswordModule(
            @PathVariable String username,
            @RequestBody Map<String, String> requestBody) {
        try {
            // Validar que la nueva contraseña está presente en el cuerpo de la solicitud
            String nuevaContrasena = requestBody.get("password");
            if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La nueva contraseña es obligatoria.");
            }

            // Obtener el usuario basado en el username
            Usuario usuario = usuarioService.obtenerUsuario(username);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado con el username: " + username);
            }

            // Actualizar la contraseña del usuario
            Usuario usuarioActualizado = usuarioService.updatePasswordModule(usuario, nuevaContrasena);

            return ResponseEntity.ok(usuarioActualizado);

        } /*catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } */catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al actualizar la contraseña. Contacte al administrador.");
        }
    }


    /**
     * @param username
     * @param usuario
     * @param rolID    {
     *                 "documento":"14703892",
     *                 "email":"izquierdin666@hotmail.com",
     *                 "nombre":"Andrés Izquierdo",
     *                 "enabled":1
     *                 }
     * @return
     */

    @PutMapping("/update/{username}/{rolID}")
    public ResponseEntity<?> update(@PathVariable String username, @RequestBody Usuario usuario, @PathVariable long rolID) {
        try {
            Usuario usuarioExistente = usuarioService.obtenerUsuario(username);
            if (usuarioExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            Rol rol = rolService.get(rolID);

            if (rol == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
            }

            Usuario nuevoUsuario = usuarioService.actualizarUsuario(username, usuario, rol);
            return ResponseEntity.ok(nuevoUsuario);

        } /*catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }*/catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar el usuario: " + ex.getMessage());
        }
    }

}
