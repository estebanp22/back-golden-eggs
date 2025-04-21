package com.goldeneggs.User;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin("*")
public class UserController {
/*
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * @param user{ "documento":"14703892",
     *                 "email":"izquierdin666@hotmail.com",
     *                 "nombre":"Andrés Izquierdo",
     *                 "enabled":1
     *                 }
     * @param rol_id   /api/v1/user/save/2
     *                 <p>
     *                 Guarda a un nuevo administrador asignandole el rol
     *                 recordar que username siempre es el mismo número de documento, así mismo la contraseña, ya que el usuario debe cambiarla en el login
     * @return Ok or Internal Error
     */

    /*
    @PostMapping("/save/{rol_id}")
    public ResponseEntity<User> save(@RequestBody User user, @PathVariable Long rol_id) {
        try {
            Role role = roleService.get(rol_id);
            if (role == null) {
                return ResponseEntity.notFound().build();
            } else {
                String passEncriptada = passwordEncoder.encode(user.getId().toString());
                user.setPassword(passEncriptada);
                user.setUsername(String.valueOf(user.getId()));
                user.setEnabled(true);
                UserRole userRole = new UserRole();
                userRole.setRole(role);
                userRole.setUser(user);
                Set<UserRole> usersRoles = Collections.singleton(userRole);
                User userGuardado = userService.saveUser(user, usersRoles);
                return ResponseEntity.ok(userGuardado);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get/{username}")
    public ResponseEntity<User> get(@PathVariable String username) {
        try {
            User user = userService.getUser(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAll() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * @param username Activa a un administrador cambiando el atributo enabled a true permitiéndole iniciar sesión de nuevo
     * @return Ok, not found or Internal Error
     */
/*
    @PutMapping("/active/{username}")
    public ResponseEntity<User> activeUser(@PathVariable String username) {
        try {
            User user = userService.getUser(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            userService.activateUser(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * @param username
     * @return Desactiva a un administrador cambiando el atributo enabled a false, esto impide que el administrador pueda iniciar sesión.
     */
    /*
    @PutMapping("/disable/{username}")
    public ResponseEntity<User> delete(@PathVariable String username) {
        try {
            User user = userService.getUser(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            userService.disableUser(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updatePassword/{username}")
    public ResponseEntity<?> updatePassword(@PathVariable String username, @RequestBody Map<String, String> request) {
        try {
            User user = userService.getUser(username);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            String email = request.get("email");
            User newUser = userService.updatePassword(user, email);

            return ResponseEntity.ok(newUser);
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
    /*
    @PutMapping("/updatePasswordModule/{username}")
    public ResponseEntity<?> updatePasswordModule(
            @PathVariable String username,
            @RequestBody Map<String, String> requestBody) {
        try {
            // Validar que la nueva contraseña está presente en el cuerpo de la solicitud
            String newPassword = requestBody.get("password");
            if (newPassword == null || newPassword.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La nueva contraseña es obligatoria.");
            }

            // Obtener el usuario basado en el username
            User user = userService.getUser(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado con el username: " + username);
            }

            // Actualizar la contraseña del usuario
            User userUpdated = userService.updatePasswordModule(user, newPassword);

            return ResponseEntity.ok(userUpdated);

        } /*catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } */
    /*catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al actualizar la contraseña. Contacte al administrador.");
        }
    }


    /**
     * @param username
     * @param user
     * @param rolID    {
     *                 "documento":"14703892",
     *                 "email":"izquierdin666@hotmail.com",
     *                 "nombre":"Andrés Izquierdo",
     *                 "enabled":1
     *                 }
     * @return
     */
    /*

    @PutMapping("/update/{username}/{rolID}")
    public ResponseEntity<?> update(@PathVariable String username, @RequestBody User user, @PathVariable long rolID) {
        try {
            User userExisting = userService.getUser(username);
            if (userExisting == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            Role role = roleService.get(rolID);

            if (role == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
            }

            User newUser = userService.updateUser(username, user, role);
            return ResponseEntity.ok(newUser);

        } /*catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }*/
    /*catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar el usuario: " + ex.getMessage());
        }
    }

     */

}
