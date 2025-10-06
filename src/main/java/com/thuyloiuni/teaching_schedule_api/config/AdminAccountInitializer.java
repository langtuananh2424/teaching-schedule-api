package com.thuyloiuni.teaching_schedule_api.config;

import com.thuyloiuni.teaching_schedule_api.entity.Department;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.repository.LecturerRepository;
import com.thuyloiuni.teaching_schedule_api.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

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
        // 1. Tìm kiếm tất cả các tài khoản có vai trò là ADMIN
        List<Lecturer> adminAccounts = lecturerRepository.findByRole(RoleType.ADMIN);

        // 2. Nếu không tìm thấy tài khoản ADMIN nào
        if (adminAccounts.isEmpty()) {
            log.info("Không tìm thấy tài khoản ADMIN nào. Đang tiến hành tạo tài khoản ADMIN mặc định...");

            // 3. Tạo một đối tượng Lecturer mới
            Lecturer adminUser = new Lecturer();
            adminUser.setLecturerCode("ADMIN"); // Mã giảng viên cho admin
            adminUser.setFullName("Quản trị viên"); // Tên đầy đủ
            adminUser.setEmail("admin@thuyloi.edu.vn"); // Email đăng nhập

            // 4. Mã hóa mật khẩu trước khi lưu
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu mặc định, nên đổi sau lần đăng nhập đầu tiên

            // 5. Gán vai trò ADMIN
            adminUser.setRole(RoleType.ADMIN);

            // 6. Gán các giá trị not-null khác nếu có (ví dụ: Department)
            Department defaultDept = departmentRepository.findById(1)
                    .orElseGet(() -> {
                        log.info("Không tìm thấy Khoa mặc định (ID=1). Đang tạo Khoa 'Công nghệ thông tin'...");
                        Department newDept = new Department();
                        newDept.setDepartmentName("Công nghệ thông tin");
                        return departmentRepository.save(newDept);
                    });
            adminUser.setDepartment(defaultDept);

            // 7. Lưu tài khoản vào cơ sở dữ liệu
            lecturerRepository.save(adminUser);

            log.info("====================================================================");
            log.info("Đã tạo tài khoản ADMIN mặc định thành công.");
            log.info("Email: admin@thuyloi.edu.vn");
            log.info("Mật khẩu: admin123");
            log.info("VUI LÒNG ĐỔI MẬT KHẨU SAU KHI ĐĂNG NHẬP LẦN ĐẦU TIÊN!");
            log.info("====================================================================");
        } else {
            log.info("Đã tìm thấy {} tài khoản ADMIN. Bỏ qua việc tạo tài khoản mặc định.", adminAccounts.size());
        }
    }
}
