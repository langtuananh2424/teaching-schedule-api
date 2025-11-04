package com.thuyloiuni.teaching_schedule_api.config;

import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.User;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("!local") // Only run when the profile is NOT 'local'
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@thuyloi.edu.vn").isEmpty()) {
            log.info("No ADMIN account found. Creating a default ADMIN account...");

            // 1. Create the User entity for authentication
            User adminUser = new User();
            adminUser.setEmail("admin@thuyloi.edu.vn");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(RoleType.ADMIN);
            User savedUser = userRepository.save(adminUser);

            // 2. Create the Lecturer entity for business logic and link it to the User
            Department defaultDept = departmentRepository.findById(1)
                    .orElseGet(() -> {
                        log.info("Default Department (ID=1) not found. Creating 'Information Technology' department...");
                        Department newDept = new Department();
                        newDept.setDepartmentName("Công nghệ thông tin");
                        return departmentRepository.save(newDept);
                    });

            Lecturer adminLecturerProfile = new Lecturer();
            adminLecturerProfile.setLecturerCode("ADMIN");
            adminLecturerProfile.setFullName("Administrator");
            adminLecturerProfile.setDepartment(defaultDept);
            adminLecturerProfile.setUser(savedUser); // Link to the created user
            lecturerRepository.save(adminLecturerProfile);

            log.info("====================================================================");
            log.info("Default ADMIN account created successfully.");
            log.info("Email: admin@thuyloi.edu.vn");
            log.info("Password: admin123");
            log.info("PLEASE CHANGE THE PASSWORD AFTER THE FIRST LOGIN!");
            log.info("====================================================================");
        } else {
            log.info("Found ADMIN account. Skipping default account creation.");
        }
    }
}
