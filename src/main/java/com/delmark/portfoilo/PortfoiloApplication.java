package com.delmark.portfoilo;

import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.UserRepository;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Push
@SpringBootApplication
public class PortfoiloApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(PortfoiloApplication.class, args);
	}

	// Только для разработки, добавляет администратора с именем testAdmin и паролем adminPass
	@Bean
	CommandLineRunner run(RolesRepository rolesRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return (args) -> {
			if (userRepository.existsByUsername("testAdmin")) return;

			Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

			HashSet<Role> roles = new HashSet<>();
			roles.add(adminRole);

			User user = new User().
					setUsername("testAdmin").
					setSurname("Админович").
					setMiddleName("").
					setName("Администратор").
					setPassword(passwordEncoder.encode("adminPass")).
					setEnabled(true).
					setRoles(roles);

			userRepository.save(user);
		};
	}

}
