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
import java.time.YearMonth;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentRepository studentRepository;
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
        if (lecturerRepository.findByEmail("test@thuyloi.edu.vn").isPresent()) {
            log.info("Data already seeded. Skipping.");
            return;
        }

        log.info("Start seeding extensive data...");

        // 1. Core Data
        List<Department> departments = createDepartments();
        List<Lecturer> lecturers = createLecturers(departments);
        List<StudentClass> studentClasses = createStudentClasses();
        List<Subject> subjects = createSubjects(departments);
        createStudents(studentClasses);
        List<Assignment> assignments = createAssignments(lecturers, subjects, studentClasses);

        // 2. Schedule Data for Current Month
        createSchedulesForCurrentMonth(assignments);

        // 3. Contextual Data (Absence, Makeup)
        createContextualData();

        log.info("Data seeding finished successfully.");
    }

    private List<Department> createDepartments() {
        return List.of(
                createDepartment("Công nghệ thông tin"),
                createDepartment("Kinh tế và Quản lý"),
                createDepartment("Công trình"),
                createDepartment("Cơ khí")
        );
    }

    private List<Lecturer> createLecturers(List<Department> departments) {
        List<Lecturer> lecturers = new ArrayList<>();
        lecturers.add(createLecturer("Admin", "ADMIN01", "admin@thuyloi.edu.vn", "admin", departments.get(0), RoleType.ADMIN));
        lecturers.add(createLecturer("TEST", "TEST01", "test@thuyloi.edu.vn", "test1234", departments.get(0), RoleType.LECTURER));

        for (int i = 0; i < 15; i++) {
            lecturers.add(createLecturer(
                    faker.name().fullName(),
                    faker.code().ean8(),
                    faker.internet().emailAddress(),
                    "password",
                    departments.get(i % departments.size()),
                    RoleType.LECTURER)
            );
        }
        return lecturers;
    }

    private List<StudentClass> createStudentClasses() {
        List<StudentClass> classes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            classes.add(createStudentClass("63TH" + i, "63TH" + i, "K63"));
            classes.add(createStudentClass("62KT" + i, "62KT" + i, "K62"));
        }
        return classes;
    }

    private void createStudents(List<StudentClass> studentClasses) {
        for (StudentClass sc : studentClasses) {
            for (int i = 0; i < 30; i++) {
                createStudent(faker.name().fullName(), "SV" + sc.getClassCode() + String.format("%02d", i), sc);
            }
        }
    }

    private List<Subject> createSubjects(List<Department> departments) {
        return List.of(
                createSubject("Lập trình Web Java", "IT4440", 3, departments.get(0)),
                createSubject("Python cho KH-Kỹ thuật", "IT3930", 2, departments.get(0)),
                createSubject("Kinh tế vi mô", "KT101", 3, departments.get(1)),
                createSubject("Marketing căn bản", "KT202", 2, departments.get(1)),
                createSubject("Sức bền vật liệu", "CT201", 3, departments.get(2)),
                createSubject("Cơ học đất", "CT301", 3, departments.get(2)),
                createSubject("Vẽ kỹ thuật", "ME201", 2, departments.get(3))
        );
    }

    private List<Assignment> createAssignments(List<Lecturer> lecturers, List<Subject> subjects, List<StudentClass> studentClasses) {
        List<Assignment> assignments = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            assignments.add(createAssignment(
                    lecturers.get(random.nextInt(lecturers.size())),
                    subjects.get(random.nextInt(subjects.size())),
                    studentClasses.get(random.nextInt(studentClasses.size())),
                    15, 30));
        }
        return assignments;
    }

    private void createSchedulesForCurrentMonth(List<Assignment> assignments) {
        if (assignments.isEmpty()) return;
        Random random = new Random();
        YearMonth currentMonth = YearMonth.now();

        for (LocalDate date = currentMonth.atDay(1); date.isBefore(currentMonth.atEndOfMonth().plusDays(1)); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) continue;

            int schedulesPerDay = 2 + random.nextInt(3); // 2-4 schedules per day
            for (int i = 0; i < schedulesPerDay; i++) {
                Assignment assignment = assignments.get(random.nextInt(assignments.size()));
                int lessonOrder = i + 1;
                int startPeriod = (i % 2 == 0) ? 1 : 7; // Morning or afternoon

                Schedule schedule = createSchedule(assignment, date.atTime(startPeriod == 1 ? 7 : 13, 0), lessonOrder, startPeriod, startPeriod + 2, "C1-" + (301 + random.nextInt(10)));

                // If schedule is in the past, create attendance
                if (schedule.getSessionDate().isBefore(LocalDateTime.now())) {
                    List<Student> students = studentRepository.findByStudentClass(assignment.getStudentClass());
                    if (!students.isEmpty()) {
                        schedule.setStatus(ScheduleStatus.TAUGHT);
                        scheduleRepository.save(schedule);
                        for (Student student : students) {
                            createAttendance(schedule, student, random.nextInt(100) < 90); // 90% present
                        }
                    }
                }
            }
        }
    }

    private void createContextualData() {
        List<Schedule> schedules = scheduleRepository.findByStatus(ScheduleStatus.NOT_TAUGHT);
        if (schedules.size() < 3) return;
        Random random = new Random();

        Lecturer admin = lecturerRepository.findByEmail("admin@thuyloi.edu.vn").orElse(null);
        if (admin == null) return;

        // PENDING request with makeup
        Schedule sched1 = schedules.get(0);
        createAbsenceRequest(sched1.getAssignment().getLecturer(), sched1, "Tham dự hội thảo khoa học", ApprovalStatus.PENDING);
        createMakeupSession(sched1, sched1.getSessionDate().plusDays(5), 1, 3, "H1-101");

        // APPROVED request
        Schedule sched2 = schedules.get(1);
        AbsenceRequest req2 = createAbsenceRequest(sched2.getAssignment().getLecturer(), sched2, "Nghỉ ốm", ApprovalStatus.APPROVED);
        req2.setApprover(admin);
        absenceRequestRepository.save(req2);
        sched2.setStatus(ScheduleStatus.ABSENT_APPROVED);
        scheduleRepository.save(sched2);

        // REJECTED request
        Schedule sched3 = schedules.get(2);
        AbsenceRequest req3 = createAbsenceRequest(sched3.getAssignment().getLecturer(), sched3, "Lý do cá nhân", ApprovalStatus.REJECTED);
        req3.setApprover(admin);
        absenceRequestRepository.save(req3);
    }


    // --- Helper Methods ---

    private Department createDepartment(String name) {
        Department d = new Department();
        d.setDepartmentName(name);
        return departmentRepository.save(d);
    }

    private Lecturer createLecturer(String fullName, String code, String email, String pass, Department dept, RoleType role) {
        Lecturer l = new Lecturer();
        l.setFullName(fullName); l.setLecturerCode(code); l.setEmail(email);
        l.setPassword(passwordEncoder.encode(pass));
        l.setDepartment(dept); l.setRole(role);
        return lecturerRepository.save(l);
    }

    private StudentClass createStudentClass(String code, String name, String semester) {
        StudentClass sc = new StudentClass();
        sc.setClassCode(code); sc.setClassName(name); sc.setSemester(semester);
        return studentClassRepository.save(sc);
    }

    private void createStudent(String fullName, String code, StudentClass sc) {
        Student s = new Student();
        s.setFullName(fullName); s.setStudentCode(code); s.setStudentClass(sc);
        studentRepository.save(s);
    }

    private Subject createSubject(String name, String code, int credits, Department dept) {
        Subject s = new Subject();
        s.setSubjectName(name); s.setSubjectCode(code); s.setCredits(credits); s.setDepartment(dept);
        return subjectRepository.save(s);
    }

    private Assignment createAssignment(Lecturer l, Subject s, StudentClass sc, int th, int pr) {
        Assignment a = new Assignment();
        a.setLecturer(l); a.setSubject(s); a.setStudentClass(sc);
        a.setTheorySession(th); a.setPracticeSession(pr);
        return assignmentRepository.save(a);
    }

    private Schedule createSchedule(Assignment a, LocalDateTime dt, int order, int start, int end, String room) {
        Schedule s = new Schedule();
        s.setAssignment(a); s.setSessionDate(dt); s.setLessonOrder(order);
        s.setStartPeriod(start); s.setEndPeriod(end); s.setClassroom(room);
        s.setStatus(ScheduleStatus.NOT_TAUGHT);
        return scheduleRepository.save(s);
    }

    private AbsenceRequest createAbsenceRequest(Lecturer l, Schedule s, String reason, ApprovalStatus status) {
        AbsenceRequest r = new AbsenceRequest();
        r.setLecturer(l); r.setSchedule(s); r.setReason(reason);
        r.setApprovalStatus(status); r.setCreatedAt(LocalDateTime.now());
        return absenceRequestRepository.save(r);
    }

    private void createMakeupSession(Schedule absent, LocalDateTime dt, int start, int end, String room) {
        MakeupSession ms = new MakeupSession();
        ms.setAbsentRequest(absent); ms.setMakeupDate(dt);
        ms.setMakeupStartPeriod(start); ms.setMakeupEndPeriod(end); ms.setMakeupClassroom(room);
        ms.setApprovalStatus(ApprovalStatus.PENDING);
        makeupSessionRepository.save(ms);
    }

    private void createAttendance(Schedule schedule, Student student, boolean isPresent) {
        Attendance a = new Attendance();
        a.setSchedule(schedule); a.setStudent(student); a.setIsPresent(isPresent);
        a.setTimestamp(schedule.getSessionDate());
        attendanceRepository.save(a);
    }
}
