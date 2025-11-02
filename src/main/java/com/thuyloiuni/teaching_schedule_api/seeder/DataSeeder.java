package com.thuyloiuni.teaching_schedule_api.seeder;

import com.github.javafaker.Faker;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import com.thuyloiuni.teaching_schedule_api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SemesterRepository semesterRepository;
    private final DepartmentRepository departmentRepository;
    private final LecturerRepository lecturerRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AssignmentRepository assignmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final AbsenceRequestRepository absenceRequestRepository;
    private final MakeupSessionRepository makeupSessionRepository;
    private final AttendanceRepository attendanceRepository;
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

        // 2. Create Assignments
        createAssignments(semesters, lecturers, subjects, studentClasses);

        // 3. Generate Schedules for all assignments
        createSchedulesForAllAssignments();

        // 4. Create Specific Scenarios for the test user
        createSpecificScenarios(lecturers, semesters, subjects, studentClasses);

        log.info("Data seeding finished successfully.");
    }

    // =================================================================
    // =========== CORE DATA CREATION METHODS ==========================
    // =================================================================

    private List<Semester> createSemesters() {
        log.info("Creating Semesters for 2024-2026...");
        List<Semester> semesters = new ArrayList<>();
        semesters.add(createSemester("Học kỳ I", "2024-2025", LocalDate.of(2024, 9, 2), LocalDate.of(2025, 1, 13)));
        semesters.add(createSemester("Học kỳ II", "2024-2025", LocalDate.of(2025, 1, 20), LocalDate.of(2025, 6, 28)));
        semesters.add(createSemester("Học kỳ I", "2025-2026", LocalDate.of(2025, 9, 8), LocalDate.of(2026, 1, 19)));
        semesters.add(createSemester("Học kỳ II", "2025-2026", LocalDate.of(2026, 1, 26), LocalDate.of(2026, 7, 4)));
        return semesters;
    }

    private List<Department> createDepartments() {
        log.info("Creating 4 Departments...");
        return List.of(
                createDepartment("Công nghệ thông tin"),
                createDepartment("Kỹ thuật Xây dựng"),
                createDepartment("Kinh tế"),
                createDepartment("Cơ khí")
        );
    }

    private List<Lecturer> createLecturers(List<Department> departments) {
        log.info("Creating Lecturers and Managers...");
        List<Lecturer> lecturers = new ArrayList<>();
        // Admin User
        lecturers.add(createLecturer("Admin", "ADMIN", "admin@thuyloi.edu.vn", "admin123", departments.get(0), RoleType.ADMIN));
        // Test User (Lecturer)
        lecturers.add(createLecturer("Test Lecturer", "TESTGV", "test@thuyloi.edu.vn", "test1234", departments.get(0), RoleType.LECTURER));

        // Create 2 Lecturers and 1 Manager per department
        lecturers.add(createLecturer("Nguyễn Văn An", "GV01", "an.nv@thuyloi.edu.vn", "password", departments.get(0), RoleType.MANAGER)); // Manager for IT
        lecturers.add(createLecturer("Trần Thị Bình", "GV02", "binh.tt@thuyloi.edu.vn", "password", departments.get(0), RoleType.LECTURER));
        
        lecturers.add(createLecturer("Lê Văn Cường", "GV03", "cuong.lv@thuyloi.edu.vn", "password", departments.get(1), RoleType.MANAGER)); // Manager for Civil Eng.
        lecturers.add(createLecturer("Phạm Thị Dung", "GV04", "dung.pt@thuyloi.edu.vn", "password", departments.get(1), RoleType.LECTURER));
        
        lecturers.add(createLecturer("Hoàng Văn Em", "GV05", "em.hv@thuyloi.edu.vn", "password", departments.get(2), RoleType.MANAGER)); // Manager for Economics
        lecturers.add(createLecturer("Vũ Thị Giang", "GV06", "giang.vt@thuyloi.edu.vn", "password", departments.get(2), RoleType.LECTURER));
        
        lecturers.add(createLecturer("Đỗ Văn Hùng", "GV07", "hung.dv@thuyloi.edu.vn", "password", departments.get(3), RoleType.MANAGER)); // Manager for Mechanical Eng.
        lecturers.add(createLecturer("Lại Thị Kiều", "GV08", "kieu.lt@thuyloi.edu.vn", "password", departments.get(3), RoleType.LECTURER));
        return lecturers;
    }

    private List<StudentClass> createStudentClasses() {
        log.info("Creating 5 Student Classes...");
        return List.of(
                createStudentClass("65K1-CNTT", "65K1-CNTT", "K65"),
                createStudentClass("65K2-XDDD", "65K2-XDDD", "K65"),
                createStudentClass("66KT1-QTKD", "66KT1-QTKD", "K66"),
                createStudentClass("66CK1-CKCT", "66CK1-CKCT", "K66"),
                createStudentClass("67K3-CNTT", "67K3-CNTT", "K67")
        );
    }

    private void createStudents(List<StudentClass> studentClasses) {
        log.info("Creating 100 Students (20 per class)...");
        for (StudentClass sc : studentClasses) {
            for (int i = 1; i <= 20; i++) {
                String studentCode = sc.getClassCode() + String.format("%03d", i);
                createStudent(faker.name().fullName(), studentCode, sc);
            }
        }
    }

    private List<Subject> createSubjects(List<Department> departments) {
        log.info("Creating 12 Subjects (3 per department)...");
        List<Subject> subjects = new ArrayList<>();
        // CNTT
        subjects.add(createSubject("Lập trình Java", "IT4440", 3, 30, 15, departments.get(0)));
        subjects.add(createSubject("Cơ sở dữ liệu", "IT3080", 3, 30, 15, departments.get(0)));
        subjects.add(createSubject("Mạng máy tính", "IT3020", 3, 45, 0, departments.get(0)));
        // Xây dựng
        subjects.add(createSubject("Sức bền vật liệu", "CE2001", 4, 45, 15, departments.get(1)));
        subjects.add(createSubject("Cơ học kết cấu", "CE3002", 4, 45, 15, departments.get(1)));
        subjects.add(createSubject("Vật liệu xây dựng", "CE1000", 2, 30, 0, departments.get(1)));
        // Kinh tế
        subjects.add(createSubject("Kinh tế vi mô", "ECO201", 3, 45, 0, departments.get(2)));
        subjects.add(createSubject("Quản trị học", "MAN202", 3, 45, 0, departments.get(2)));
        subjects.add(createSubject("Marketing căn bản", "MKT301", 3, 30, 15, departments.get(2)));
        // Cơ khí
        subjects.add(createSubject("Cơ lý thuyết", "ME2001", 3, 45, 0, departments.get(3)));
        subjects.add(createSubject("Vẽ kỹ thuật", "ME1002", 2, 15, 30, departments.get(3)));
        subjects.add(createSubject("Thủy lực đại cương", "ME3010", 3, 30, 15, departments.get(3)));
        return subjects;
    }

    private void createAssignments(List<Semester> semesters, List<Lecturer> lecturers, List<Subject> subjects, List<StudentClass> studentClasses) {
        log.info("Creating General Assignments (each lecturer teaches >= 2 subjects)...");

        List<Lecturer> teachingStaff = lecturers.stream()
                .filter(l -> l.getRole() == RoleType.LECTURER && !l.getEmail().equals("test@thuyloi.edu.vn"))
                .collect(Collectors.toList());

        Map<Department, List<Subject>> subjectsByDept = subjects.stream()
                .collect(Collectors.groupingBy(Subject::getDepartment));

        int semesterIndex = 0;
        for (Lecturer lecturer : teachingStaff) {
            Department dept = lecturer.getDepartment();
            List<Subject> availableSubjects = new ArrayList<>(subjectsByDept.getOrDefault(dept, Collections.emptyList()));
            Collections.shuffle(availableSubjects);

            if (availableSubjects.size() >= 2) {
                createAssignment(semesters.get(semesterIndex % semesters.size()), availableSubjects.get(0), lecturer, getRandomClass(studentClasses));
                createAssignment(semesters.get((semesterIndex + 1) % semesters.size()), availableSubjects.get(1), lecturer, getRandomClass(studentClasses));
            } else if (!availableSubjects.isEmpty()) {
                createAssignment(semesters.get(semesterIndex % semesters.size()), availableSubjects.get(0), lecturer, getRandomClass(studentClasses));
            }
            semesterIndex++;
        }
    }

    private void createSchedulesForAllAssignments() {
        log.info("Creating Schedules for all general assignments...");
        List<Assignment> allAssignments = assignmentRepository.findAll().stream()
                .filter(a -> !a.getLecturer().getEmail().equals("test@thuyloi.edu.vn"))
                .collect(Collectors.toList());

        for (Assignment assignment : allAssignments) {
            createSchedulesForOneAssignment(assignment, assignment.getSemester().getStartDate());
        }
    }

    private void createSpecificScenarios(List<Lecturer> lecturers, List<Semester> semesters, List<Subject> subjects, List<StudentClass> studentClasses) {
        log.info("Creating Specific Scenarios for test@thuyloi.edu.vn...");

        Lecturer testLecturer = lecturers.stream()
                .filter(l -> l.getEmail().equals("test@thuyloi.edu.vn"))
                .findFirst().orElseThrow(() -> new IllegalStateException("Test lecturer not found!"));

        Semester currentSemester = findCurrentSemester(semesters);
        
        List<Subject> testLecturerSubjects = subjects.stream()
                .filter(s -> s.getDepartment().equals(testLecturer.getDepartment()))
                .collect(Collectors.toList());
        Collections.shuffle(testLecturerSubjects);

        if (testLecturerSubjects.size() < 3) {
            log.error("Not enough subjects in department for test lecturer!");
            return;
        }

        Assignment assignment1 = createAssignment(currentSemester, testLecturerSubjects.get(0), testLecturer, studentClasses.get(0));
        createAssignment(currentSemester, testLecturerSubjects.get(1), testLecturer, studentClasses.get(1));
        createAssignment(currentSemester, testLecturerSubjects.get(2), testLecturer, studentClasses.get(2));
        
        List<Schedule> testSchedules = createSchedulesForOneAssignment(assignment1, LocalDate.now().withDayOfMonth(1));

        if (testSchedules.size() < 3) {
            log.warn("Not enough schedules generated in the current month for test lecturer.");
            return;
        }
        
        log.info("Creating Absence/Makeup/Attendance scenario for test lecturer...");
        Schedule scheduleToMiss = testSchedules.get(1); 
        createAbsenceRequest(testLecturer, scheduleToMiss, "Tham dự hội thảo khoa học", ApprovalStatus.APPROVED, ApprovalStatus.APPROVED);
        scheduleToMiss.setStatus(ScheduleStatus.ABSENT_APPROVED);
        scheduleRepository.save(scheduleToMiss);

        createMakeupSession(scheduleToMiss, scheduleToMiss.getSessionDate().plusWeeks(1), 7, 9, "H1-202", ApprovalStatus.APPROVED, ApprovalStatus.APPROVED);

        Schedule scheduleTaught = testSchedules.get(0);
        scheduleTaught.setStatus(ScheduleStatus.TAUGHT);
        scheduleRepository.save(scheduleTaught);
        
        List<Student> studentsOfClass = studentRepository.findByStudentClass(scheduleTaught.getAssignment().getStudentClass());
        for (int i = 0; i < studentsOfClass.size(); i++) {
            createAttendance(scheduleTaught, studentsOfClass.get(i), i % 10 != 0);
        }
        log.info("Created attendance data for session ID: {}", scheduleTaught.getSessionId());
    }
    
    // ... (Single entity creation helpers are mostly unchanged) ...

    private AbsenceRequest createAbsenceRequest(Lecturer l, Schedule s, String reason, ApprovalStatus managerStatus, ApprovalStatus academicStatus) {
        AbsenceRequest r = new AbsenceRequest();
        r.setLecturer(l);
        r.setSchedule(s);
        r.setReason(reason);
        r.setManagerApproval(managerStatus);
        r.setAcademicAffairsApproval(academicStatus);
        return absenceRequestRepository.save(r);
    }

    private void createMakeupSession(Schedule absent, LocalDateTime dt, int start, int end, String room, ApprovalStatus managerStatus, ApprovalStatus academicStatus) {
        MakeupSession ms = new MakeupSession();
        ms.setAbsentSchedule(absent);
        ms.setMakeupDate(dt);
        ms.setMakeupStartPeriod(start);
        ms.setMakeupEndPeriod(end);
        ms.setMakeupClassroom(room);
        ms.setManagerApproval(managerStatus);
        ms.setAcademicAffairsApproval(academicStatus);
        makeupSessionRepository.save(ms);
    }
    // ... (rest of the file is unchanged) ...
    private Semester createSemester(String name, String year, LocalDate start, LocalDate end) {
        Semester s = new Semester();
        s.setName(name);
        s.setAcademicYear(year);
        s.setStartDate(start);
        s.setEndDate(end);
        return semesterRepository.save(s);
    }

    private Department createDepartment(String name) {
        Department d = new Department();
        d.setDepartmentName(name);
        return departmentRepository.save(d);
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

    private StudentClass createStudentClass(String code, String name, String semester) {
        StudentClass sc = new StudentClass();
        sc.setClassCode(code);
        sc.setClassName(name);
        sc.setSemester(semester);
        return studentClassRepository.save(sc);
    }

    private void createStudent(String fullName, String code, StudentClass sc) {
        Student s = new Student();
        s.setFullName(fullName);
        s.setStudentCode(code);
        s.setStudentClass(sc);
        studentRepository.save(s);
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

    private Assignment createAssignment(Semester semester, Subject subject, Lecturer lecturer, StudentClass studentClass) {
        Assignment a = new Assignment();
        a.setSemester(semester);
        a.setSubject(subject);
        a.setLecturer(lecturer);
        a.setStudentClass(studentClass);
        return assignmentRepository.save(a);
    }

    private void createAttendance(Schedule schedule, Student student, boolean isPresent) {
        Attendance a = new Attendance();
        a.setSchedule(schedule);
        a.setStudent(student);
        a.setIsPresent(isPresent);
        a.setTimestamp(schedule.getSessionDate());
        attendanceRepository.save(a);
    }

    private List<Schedule> createSchedulesForOneAssignment(Assignment assignment, LocalDate startDate) {
        int totalPeriods = assignment.getSubject().getTheoryPeriods() + assignment.getSubject().getPracticePeriods();
        int periodsPerSession = 2; // Assuming 2 periods per session
        int totalSessions = (int) Math.ceil((double) totalPeriods / periodsPerSession);

        LocalDate scheduleDate = startDate;
        List<Schedule> schedules = new ArrayList<>();

        for (int i = 1; i <= totalSessions; i++) {
            // Find the next valid teaching day (Mon-Fri)
            while (scheduleDate.getDayOfWeek() == DayOfWeek.SATURDAY || scheduleDate.getDayOfWeek() == DayOfWeek.SUNDAY || scheduleDate.isAfter(assignment.getSemester().getEndDate())) {
                scheduleDate = scheduleDate.plusDays(1);
                if (scheduleDate.isAfter(assignment.getSemester().getEndDate())) {
                    log.warn("Stopped creating schedules for assignment {} as it exceeded semester end date.", assignment.getAssignmentId());
                    return schedules;
                }
            }

            int startPeriod = (ThreadLocalRandom.current().nextBoolean()) ? 1 : 7;
            LocalDateTime sessionDateTime = scheduleDate.atTime(startPeriod == 1 ? 7 : 13, 0);

            Schedule schedule = new Schedule();
            schedule.setAssignment(assignment);
            schedule.setSessionDate(sessionDateTime);
            schedule.setLessonOrder(i);
            schedule.setStartPeriod(startPeriod);
            schedule.setEndPeriod(startPeriod + periodsPerSession - 1);
            schedule.setClassroom("C1-30" + (1 + ThreadLocalRandom.current().nextInt(9)));
            schedule.setStatus(ScheduleStatus.NOT_TAUGHT);
            schedules.add(scheduleRepository.save(schedule));

            scheduleDate = scheduleDate.plusDays(ThreadLocalRandom.current().nextInt(2, 5));
        }
        return schedules;
    }

    private Semester findCurrentSemester(List<Semester> semesters) {
        LocalDate now = LocalDate.now();
        return semesters.stream()
                .filter(s -> !now.isBefore(s.getStartDate()) && !now.isAfter(s.getEndDate()))
                .findFirst()
                .orElse(
                    semesters.stream()
                        .filter(s -> s.getStartDate().isAfter(now))
                        .min(Comparator.comparing(Semester::getStartDate))
                        .orElse(semesters.stream().max(Comparator.comparing(Semester::getEndDate)).get())
                );
    }

    private StudentClass getRandomClass(List<StudentClass> studentClasses) {
        return studentClasses.get(ThreadLocalRandom.current().nextInt(studentClasses.size()));
    }
}
