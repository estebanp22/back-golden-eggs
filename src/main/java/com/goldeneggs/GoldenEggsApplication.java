package com.goldeneggs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class GoldenEggsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GoldenEggsApplication.class, args);
	}


	@Override
	@Transactional
	public void run(String... args) throws Exception {
/*

		try {
			User user = new User();

			user.setId(123456789L);
			user.setUsername("admin");
			user.setPassword("123456789");
			user.setEnabled(true);
			user.setEmail("<EMAIL>");
			user.setAddress("Calle 123 # 123");
			user.setName("Administrador");
			user.setPhoneNumber("123456789");

			String passEncript = passwordEncoder.encode(user.getPassword());
			user.setPassword(passEncript);

			Role role = roleService.get(1L);
			//Role role = new Role();
			//role.setRoleName("Admin");
			//Role roleSaved = roleService.insert(role);

			Set<UserRole> userRoles = new HashSet<>();
			UserRole userRole = new UserRole();
			userRole.setRole(role);
			userRole.setUser(user);
			userRoles.add(userRole);

			User userSaved = userService.saveUser(user, userRoles);
			System.out.println(userSaved.getUsername() + " guardado correctamente");
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();

		}
 */

	}
}
