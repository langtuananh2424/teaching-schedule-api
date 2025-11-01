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

        // 4. Create Specific Scenarios
        createSpecificScenarios(lecturers, semesters, subjects, studentClasses);

        log.info("Data seeding finished successfully.");
    }

    private void createSpecificScenarios(List<Lecturer> lecturers, List<Semester> semesters, List<Subject> subjects, List<StudentClass> studentClasses) {
        log.info("Creating Specific Scenarios for test@thuyloi.edu.vn...");

        Lecturer testLecturer = lecturerRepository.findByEmail("test@thuyloi.edu.vn").orElse(null);
        if (testLecturer == null) {
            log.error("Could not find test lecturer!");
            return;
        }

        Semester recentSemester = semesters.stream()
                .filter(s -> s.getAcademicYear().equals("2024-2025") && s.getName().equals("Học kỳ I"))
                .findFirst().orElse(semesters.get(2));

        Assignment testAssignment = createAssignment(recentSemester, subjects.get(1), testLecturer, studentClasses.get(0));

        List<Schedule> testSchedules = createSchedulesForOneAssignment(testAssignment);

        if (testSchedules.size() < 3) {
            log.warn("Not enough schedules generated for test lecturer to create scenarios.");
            return;
        }

        Schedule scheduleToMiss = testSchedules.get(1);
        createAbsenceRequest(testLecturer, scheduleToMiss, "Tham dự hội thảo khoa học", ApprovalStatus.APPROVED, ApprovalStatus.APPROVED);
        scheduleToMiss.setStatus(ScheduleStatus.ABSENT_APPROVED);
        scheduleRepository.save(scheduleToMiss);

        createMakeupSession(scheduleToMiss, scheduleToMiss.getSessionDate().plusWeeks(1), 7, 9, "H1-202", ApprovalStatus.APPROVED, ApprovalStatus.APPROVED);

        Schedule scheduleTaught = testSchedules.get(0);
        if (scheduleTaught.getSessionDate().isBefore(LocalDateTime.now())) {
            scheduleTaught.setStatus(ScheduleStatus.TAUGHT);
            scheduleRepository.save(scheduleTaught);
            
            // FIX: Fetch students from the repository instead of the entity
            List<Student> studentsOfClass = studentRepository.findByStudentClass(scheduleTaught.getAssignment().getStudentClass());

            for (int i = 0; i < studentsOfClass.size(); i++) {
                createAttendance(scheduleTaught, studentsOfClass.get(i), i % 10 != 0);
            }
            log.info("Created attendance data for session ID: {}", scheduleTaught.getSessionId());
        }
    }

    // ... (The rest of the file is unchanged) ...
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
        // Add the specific test lecturer account
        lecturers.add(createLecturer("Test Lecturer", "TESTGV", "test@thuyloi.edu.vn", "test1234", departments.get(0), RoleType.LECTURER));
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
        log.info("Creating General Assignments...");
        createAssignment(semesters.get(0), subjects.get(0), lecturers.get(2), studentClasses.get(0));
        createAssignment(semesters.get(0), subjects.get(2), lecturers.get(4), studentClasses.get(2));
        createAssignment(semesters.get(1), subjects.get(1), lecturers.get(3), studentClasses.get(0));
        createAssignment(semesters.get(2), subjects.get(0), lecturers.get(3), studentClasses.get(1));
        createAssignment(semesters.get(2), subjects.get(3), lecturers.get(5), studentClasses.get(2));
        createAssignment(semesters.get(3), subjects.get(1), lecturers.get(2), studentClasses.get(1));
    }

    private Assignment createAssignment(Semester semester, Subject subject, Lecturer lecturer, StudentClass studentClass) {
        Assignment a = new Assignment();
        a.setSemester(semester);
        a.setSubject(subject);
        a.setLecturer(lecturer);
        a.setStudentClass(studentClass);
        return assignmentRepository.save(a);
    }

    private void createSchedulesForAllAssignments() {
        log.info("Creating Schedules for all general assignments...");
        List<Assignment> allAssignments = assignmentRepository.findAll();
        for (Assignment assignment : allAssignments) {
            createSchedulesForOneAssignment(assignment);
        }
    }

    private List<Schedule> createSchedulesForOneAssignment(Assignment assignment) {
        int totalPeriods = assignment.getSubject().getTheoryPeriods() + assignment.getSubject().getPracticePeriods();
        int periodsPerSession = 2;
        int totalSessions = (int) Math.ceil((double) totalPeriods / periodsPerSession);

        LocalDate startDate = assignment.getSemester().getStartDate().plusWeeks(1);
        Random random = new Random();
        List<Schedule> schedules = new ArrayList<>();

        for (int i = 1; i <= totalSessions; i++) {
            while (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY || startDate.isAfter(assignment.getSemester().getEndDate())) {
                startDate = startDate.plusDays(1);
            }

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
            schedules.add(scheduleRepository.save(schedule));

            startDate = startDate.plusDays(2 + random.nextInt(3));
        }
        return schedules;
    }

    private AbsenceRequest createAbsenceRequest(Lecturer l, Schedule s, String reason, ApprovalStatus deptStatus, ApprovalStatus ctsvStatus) {
        AbsenceRequest r = new AbsenceRequest();
        r.setLecturer(l);
        r.setSchedule(s);
        r.setReason(reason);
        r.setDepartmentApproval(deptStatus);
        r.setCtsvApproval(ctsvStatus);
        return absenceRequestRepository.save(r);
    }

    private void createMakeupSession(Schedule absent, LocalDateTime dt, int start, int end, String room, ApprovalStatus deptStatus, ApprovalStatus ctsvStatus) {
        MakeupSession ms = new MakeupSession();
        ms.setAbsentSchedule(absent);
        ms.setMakeupDate(dt);
        ms.setMakeupStartPeriod(start);
        ms.setMakeupEndPeriod(end);
        ms.setMakeupClassroom(room);
        ms.setDepartmentApproval(deptStatus);
        ms.setCtsvApproval(ctsvStatus);
        makeupSessionRepository.save(ms);
    }

    private void createAttendance(Schedule schedule, Student student, boolean isPresent) {
        Attendance a = new Attendance();
        a.setSchedule(schedule);
        a.setStudent(student);
        a.setIsPresent(isPresent);
        a.setTimestamp(schedule.getSessionDate());
        attendanceRepository.save(a);
    }
}
