package com.delmark.portfoilo;

import com.delmark.portfoilo.models.Role;
import com.delmark.portfoilo.models.User;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class PortfoiloApplication {

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

			userRepository.save(new User(1L, "testAdmin",
					passwordEncoder.encode("adminPass"),
					true,
					roles
					)
			);
		};
	}

}
