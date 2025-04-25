package com.goldeneggs;

import com.goldeneggs.Dto.RegisterDto;
import com.goldeneggs.Role.Role;
import com.goldeneggs.Role.RoleService;
import com.goldeneggs.User.User;
import com.goldeneggs.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class GoldenEggsApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GoldenEggsApplication.class, args);
	}

	@Autowired
	UserService userService;

	@Autowired
	private RoleService roleService;

	@Override
	@Transactional
	public void run(String... args) throws Exception {

/*
		try {

			RegisterDto user = new RegisterDto();

			user.setId(123456789L);
			user.setUsername("admin");
			user.setPassword("123456789");
			user.setEmail("<EMAIL>");
			user.setAddress("Calle 123 # 123");
			user.setName("Administrador");
			user.setPhoneNumber("123456789");
			user.setRoleId(1L);

			//Role role = roleService.get(1L);
			Role role = new Role();
			role.setName("Admin");
			Role roleSaved = roleService.insert(role);


			User userSaved = userService.save(user);
			System.out.println(userSaved.getUsername() + " guardado correctamente");
		} catch (ClassNotFoundException exception) {
			exception.printStackTrace();

		}



 */
	}
}
