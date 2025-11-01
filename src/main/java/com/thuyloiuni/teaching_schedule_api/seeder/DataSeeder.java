package com.thuyloiuni.teaching_schedule_api.seeder;

import com.github.javafaker.Faker;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import com.thuyloiuni.teaching_schedule_api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Profile("local")
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final SemesterRepository semesterRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final AssignmentRepository assignmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker(new Locale("vi"));

    @Override
    @Transactional
    public void run(String... args) {
        if (lecturerRepository.findByEmail("admin@thuyloi.edu.vn").isPresent()) {
            log.info("Data already seeded. Skipping.");
            return;
        }
        log.info("Start seeding data for local environment...");

        // 1. Create Core Data
        List<Semester> semesters = createSemesters();
        List<Department> departments = createDepartments();
        List<Lecturer> lecturers = createLecturers(departments);
        List<StudentClass> studentClasses = createStudentClasses();
        List<Subject> subjects = createSubjects(departments);
        createStudents(studentClasses);

        // 2. Create Assignments linking all the above data
        createAssignments(semesters, lecturers, subjects, studentClasses);

        // 3. Generate Schedules for all assignments
        createSchedulesForAllAssignments();

        log.info("Data seeding finished successfully.");
    }

    private List<Semester> createSemesters() {
        log.info("Creating Semesters...");
        List<Semester> semesters = new ArrayList<>();
        semesters.add(createSemester("Học kỳ I", "2023-2024", LocalDate.of(2023, 9, 5), LocalDate.of(2024, 1, 15)));
        semesters.add(createSemester("Học kỳ II", "2023-2024", LocalDate.of(2024, 1, 22), LocalDate.of(2024, 6, 30)));
        semesters.add(createSemester("Học kỳ I", "2024-2025", LocalDate.of(2024, 9, 2), LocalDate.of(2025, 1, 13)));
        semesters.add(createSemester("Học kỳ II", "2024-2025", LocalDate.of(2025, 1, 20), LocalDate.of(2025, 6, 28)));
        return semesters;
    }

    private Semester createSemester(String name, String year, LocalDate start, LocalDate end) {
        Semester s = new Semester();
        s.setName(name);
        s.setAcademicYear(year);
        s.setStartDate(start);
        s.setEndDate(end);
        return semesterRepository.save(s);
    }

    private List<Department> createDepartments() {
        log.info("Creating Departments...");
        return List.of(
                createDepartment("Công nghệ thông tin"),
                createDepartment("Kỹ thuật Xây dựng")
        );
    }

    private Department createDepartment(String name) {
        Department d = new Department();
        d.setDepartmentName(name);
        return departmentRepository.save(d);
    }

    private List<Lecturer> createLecturers(List<Department> departments) {
        log.info("Creating Lecturers...");
        List<Lecturer> lecturers = new ArrayList<>();
        lecturers.add(createLecturer("Admin", "ADMIN", "admin@thuyloi.edu.vn", "admin123", departments.get(0), RoleType.ADMIN));
        lecturers.add(createLecturer("Nguyễn Văn An", "GV01", "an.nv@thuyloi.edu.vn", "password", departments.get(0), RoleType.LECTURER));
        lecturers.add(createLecturer("Trần Thị Bình", "GV02", "binh.tt@thuyloi.edu.vn", "password", departments.get(0), RoleType.LECTURER));
        lecturers.add(createLecturer("Lê Văn Cường", "GV03", "cuong.lv@thuyloi.edu.vn", "password", departments.get(1), RoleType.LECTURER));
        lecturers.add(createLecturer("Phạm Thị Dung", "GV04", "dung.pt@thuyloi.edu.vn", "password", departments.get(1), RoleType.LECTURER));
        return lecturers;
    }

    private Lecturer createLecturer(String fullName, String code, String email, String pass, Department dept, RoleType role) {
        Lecturer l = new Lecturer();
        l.setFullName(fullName);
        l.setLecturerCode(code);
        l.setEmail(email);
        l.setPassword(passwordEncoder.encode(pass));
        l.setDepartment(dept);
        l.setRole(role);
        return lecturerRepository.save(l);
    }

    private List<StudentClass> createStudentClasses() {
        log.info("Creating Student Classes...");
        List<StudentClass> classes = new ArrayList<>();
        classes.add(createStudentClass("64K1-CNTT", "64K1-CNTT", "K64"));
        classes.add(createStudentClass("65K2-CNTT", "65K2-CNTT", "K65"));
        classes.add(createStudentClass("64X1-XDDD", "64X1-XDDD", "K64"));
        return classes;
    }

    private StudentClass createStudentClass(String code, String name, String semester) {
        StudentClass sc = new StudentClass();
        sc.setClassCode(code);
        sc.setClassName(name);
        sc.setSemester(semester);
        return studentClassRepository.save(sc);
    }

    private void createStudents(List<StudentClass> studentClasses) {
        log.info("Creating Students...");
        for (StudentClass sc : studentClasses) {
            for (int i = 1; i <= 10; i++) {
                String studentCode = sc.getClassCode() + String.format("%03d", i);
                createStudent(faker.name().fullName(), studentCode, sc);
            }
        }
    }

    private void createStudent(String fullName, String code, StudentClass sc) {
        Student s = new Student();
        s.setFullName(fullName);
        s.setStudentCode(code);
        s.setStudentClass(sc);
        studentRepository.save(s);
    }

    private List<Subject> createSubjects(List<Department> departments) {
        log.info("Creating Subjects...");
        List<Subject> subjects = new ArrayList<>();
        subjects.add(createSubject("Lập trình Java", "IT4440", 3, 30, 15, departments.get(0)));
        subjects.add(createSubject("Cơ sở dữ liệu", "IT3080", 3, 30, 15, departments.get(0)));
        subjects.add(createSubject("Sức bền vật liệu", "CE2001", 4, 45, 15, departments.get(1)));
        subjects.add(createSubject("Cơ học kết cấu", "CE3002", 4, 45, 15, departments.get(1)));
        return subjects;
    }

    private Subject createSubject(String name, String code, int credits, int theory, int practice, Department dept) {
        Subject s = new Subject();
        s.setSubjectName(name);
        s.setSubjectCode(code);
        s.setCredits(credits);
        s.setTheoryPeriods(theory);
        s.setPracticePeriods(practice);
        s.setDepartment(dept);
        return subjectRepository.save(s);
    }

    private void createAssignments(List<Semester> semesters, List<Lecturer> lecturers, List<Subject> subjects, List<StudentClass> studentClasses) {
        log.info("Creating Assignments...");
        // HK I, 2023-2024
        createAssignment(semesters.get(0), subjects.get(0), lecturers.get(1), studentClasses.get(0)); // An dạy Java cho 64K1
        createAssignment(semesters.get(0), subjects.get(2), lecturers.get(3), studentClasses.get(2)); // Cường dạy SBVL cho 64X1

        // HK II, 2023-2024
        createAssignment(semesters.get(1), subjects.get(1), lecturers.get(2), studentClasses.get(0)); // Bình dạy CSDL cho 64K1

        // HK I, 2024-2025
        createAssignment(semesters.get(2), subjects.get(0), lecturers.get(2), studentClasses.get(1)); // Bình dạy Java cho 65K2
        createAssignment(semesters.get(2), subjects.get(3), lecturers.get(4), studentClasses.get(2)); // Dung dạy KCKT cho 64X1

        // HK II, 2024-2025
        createAssignment(semesters.get(3), subjects.get(1), lecturers.get(1), studentClasses.get(1)); // An dạy CSDL cho 65K2
    }

    private void createAssignment(Semester semester, Subject subject, Lecturer lecturer, StudentClass studentClass) {
        Assignment a = new Assignment();
        a.setSemester(semester);
        a.setSubject(subject);
        a.setLecturer(lecturer);
        a.setStudentClass(studentClass);
        assignmentRepository.save(a);
    }

    private void createSchedulesForAllAssignments() {
        log.info("Creating Schedules for all assignments...");
        List<Assignment> allAssignments = assignmentRepository.findAll();
        for (Assignment assignment : allAssignments) {
            createSchedulesForOneAssignment(assignment);
        }
    }

    private void createSchedulesForOneAssignment(Assignment assignment) {
        int totalPeriods = assignment.getSubject().getTheoryPeriods() + assignment.getSubject().getPracticePeriods();
        int periodsPerSession = 2; // Giả định mỗi buổi học 2 tiết
        int totalSessions = (int) Math.ceil((double) totalPeriods / periodsPerSession);

        LocalDate startDate = assignment.getSemester().getStartDate().plusWeeks(1); // Bắt đầu học sau tuần đầu tiên của học kỳ
        Random random = new Random();

        for (int i = 1; i <= totalSessions; i++) {
            // Tìm ngày trong tuần tiếp theo (Thứ 2 đến Thứ 6)
            while (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY || startDate.isAfter(assignment.getSemester().getEndDate())) {
                startDate = startDate.plusDays(1);
            }

            // Ngẫu nhiên buổi sáng hoặc chiều
            int startPeriod = (random.nextBoolean()) ? 1 : 7;
            LocalDateTime sessionDateTime = startDate.atTime(startPeriod == 1 ? 7 : 13, 0);

            Schedule schedule = new Schedule();
            schedule.setAssignment(assignment);
            schedule.setSessionDate(sessionDateTime);
            schedule.setLessonOrder(i);
            schedule.setStartPeriod(startPeriod);
            schedule.setEndPeriod(startPeriod + periodsPerSession - 1);
            schedule.setClassroom("C1-30" + (1 + random.nextInt(9)));
            schedule.setStatus(ScheduleStatus.NOT_TAUGHT);
            scheduleRepository.save(schedule);

            // Chuyển sang ngày học tiếp theo, cách 2-4 ngày
            startDate = startDate.plusDays(2 + random.nextInt(3));
        }
    }
}
