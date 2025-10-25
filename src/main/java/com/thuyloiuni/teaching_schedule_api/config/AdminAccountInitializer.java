package com.thuyloiuni.teaching_schedule_api.config;

import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Profile("!local") // Chỉ chạy khi profile KHÔNG PHẢI là 'local'
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAccountInitializer implements CommandLineRunner {

    private final LecturerRepository lecturerRepository;
    private final PasswordEncoder passwordEncoder;

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<Lecturer> adminAccounts = lecturerRepository.findByRole(RoleType.ADMIN);

        if (adminAccounts.isEmpty()) {
            log.info("No ADMIN account found. Creating a default ADMIN account...");

            Lecturer adminUser = new Lecturer();
            adminUser.setLecturerCode("ADMIN");
            adminUser.setFullName("Administrator");
            adminUser.setEmail("admin@thuyloi.edu.vn");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(RoleType.ADMIN);

            Department defaultDept = departmentRepository.findById(1)
                    .orElseGet(() -> {
                        log.info("Default Department (ID=1) not found. Creating 'Information Technology' department...");
                        Department newDept = new Department();
                        newDept.setDepartmentName("Công nghệ thông tin");
                        return departmentRepository.save(newDept);
                    });
            adminUser.setDepartment(defaultDept);

            lecturerRepository.save(adminUser);

            log.info("====================================================================");
            log.info("Default ADMIN account created successfully.");
            log.info("Email: admin@thuyloi.edu.vn");
            log.info("Password: admin123");
            log.info("PLEASE CHANGE THE PASSWORD AFTER THE FIRST LOGIN!");
            log.info("====================================================================");
        } else {
            log.info("Found {} ADMIN account(s). Skipping default account creation.", adminAccounts.size());
        }
    }
}
