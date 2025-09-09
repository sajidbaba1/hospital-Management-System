package com.hms.config;

import com.hms.user.Role;
import com.hms.user.UserAccount;
import com.hms.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.findByUsername("admin").isEmpty()) {
                UserAccount u = new UserAccount();
                u.setUsername("admin");
                u.setFullName("System Admin");
                u.setRole(Role.ADMIN);
                u.setPassword(encoder.encode("admin123"));
                users.save(u);
            }
            if (users.findByUsername("dr.jane").isEmpty()) {
                UserAccount u = new UserAccount();
                u.setUsername("dr.jane");
                u.setFullName("Dr. Jane Doe");
                u.setRole(Role.DOCTOR);
                u.setPassword(encoder.encode("doctor123"));
                users.save(u);
            }
            if (users.findByUsername("reception").isEmpty()) {
                UserAccount u = new UserAccount();
                u.setUsername("reception");
                u.setFullName("Front Desk");
                u.setRole(Role.RECEPTIONIST);
                u.setPassword(encoder.encode("recep123"));
                users.save(u);
            }
        };
    }
}
