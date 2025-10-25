package com.thuyloiuni.teaching_schedule_api.seeder;

import com.github.javafaker.Faker;
import com.thuyloiuni.teaching_schedule_api.entity.*;
import com.thuyloiuni.teaching_schedule_api.model.ApprovalStatus;
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
import java.time.YearMonth;
import java.util.*;

@Profile("local")
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
        if (lecturerRepository.findByEmail("admin@thuyloi.edu.vn").isPresent()) {
            log.info("Data already seeded. Skipping.");
            return;
        }
        log.info("Start seeding extensive data for local environment...");

        List<Department> departments = createDepartments();
        List<Lecturer> lecturers = createLecturers(departments);
        List<StudentClass> studentClasses = createStudentClasses();
        List<Subject> subjects = createSubjects(departments);
        createStudents(studentClasses);
        List<Assignment> assignments = createAssignments(lecturers, subjects, studentClasses);
        createSchedulesForCurrentMonth(assignments);
        createContextualData();

        log.info("Data seeding finished successfully.");
    }

    private void createContextualData() {
        List<Schedule> schedules = scheduleRepository.findByStatus(ScheduleStatus.NOT_TAUGHT);
        if (schedules.size() < 5) return;
        Random random = new Random();

        // Scenario 1: Pending request (waiting for department)
        createAbsenceRequest(schedules.get(0).getAssignment().getLecturer(), schedules.get(0), "Đi công tác đột xuất", ApprovalStatus.PENDING, ApprovalStatus.PENDING);

        // Scenario 2: Department approved, waiting for CTSV
        createAbsenceRequest(schedules.get(1).getAssignment().getLecturer(), schedules.get(1), "Tham dự hội thảo khoa học", ApprovalStatus.APPROVED, ApprovalStatus.PENDING);

        // Scenario 3: Fully approved request with a makeup session
        Schedule sched3 = schedules.get(2);
        createAbsenceRequest(sched3.getAssignment().getLecturer(), sched3, "Nghỉ ốm", ApprovalStatus.APPROVED, ApprovalStatus.APPROVED);
        createMakeupSession(sched3, sched3.getSessionDate().plusDays(7), 1, 3, "H1-202", ApprovalStatus.APPROVED, ApprovalStatus.APPROVED);
        sched3.setStatus(ScheduleStatus.ABSENT_APPROVED);
        scheduleRepository.save(sched3);

        // Scenario 4: Rejected by department
        createAbsenceRequest(schedules.get(3).getAssignment().getLecturer(), schedules.get(3), "Việc gia đình", ApprovalStatus.REJECTED, ApprovalStatus.PENDING);

        // Scenario 5: Approved by department, but rejected by CTSV
        createAbsenceRequest(schedules.get(4).getAssignment().getLecturer(), schedules.get(4), "Lý do cá nhân không chính đáng", ApprovalStatus.APPROVED, ApprovalStatus.REJECTED);
    }

    // --- Helper Methods ---

    private AbsenceRequest createAbsenceRequest(Lecturer l, Schedule s, String reason, ApprovalStatus deptStatus, ApprovalStatus ctsvStatus) {
        AbsenceRequest r = new AbsenceRequest();
        r.setLecturer(l);
        r.setSchedule(s);
        r.setReason(reason);
        r.setDepartmentApproval(deptStatus);
        r.setCtsvApproval(ctsvStatus);
        // createdAt is set by @PrePersist
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
        // createdAt is set by @PrePersist
        makeupSessionRepository.save(ms);
    }

    // --- Unchanged Methods from here down ---

    private List<Department> createDepartments() {
        return List.of(
                createDepartment("Công nghệ thông tin"),
                createDepartment("Kinh tế và Quản lý"),
                createDepartment("Công trình"),
                createDepartment("Cơ khí")
        );
    }
    private Department createDepartment(String name) {
        Department d = new Department();
        d.setDepartmentName(name);
        return departmentRepository.save(d);
    }

    private List<Lecturer> createLecturers(List<Department> departments) {
        List<Lecturer> lecturers = new ArrayList<>();
        lecturers.add(createLecturer("Admin", "ADMIN01", "admin@thuyloi.edu.vn", "admin123", departments.get(0), RoleType.ADMIN));
        lecturers.add(createLecturer("TEST", "TEST01", "test@thuyloi.edu.vn", "test1234", departments.get(0), RoleType.LECTURER));
        for (int i = 0; i < 15; i++) {
            lecturers.add(createLecturer(faker.name().fullName(), faker.code().ean8(), faker.internet().emailAddress(), "password", departments.get(i % departments.size()), RoleType.LECTURER));
        }
        return lecturers;
    }
    private Lecturer createLecturer(String fullName, String code, String email, String pass, Department dept, RoleType role) {
        Lecturer l = new Lecturer();
        l.setFullName(fullName); l.setLecturerCode(code); l.setEmail(email);
        l.setPassword(passwordEncoder.encode(pass));
        l.setDepartment(dept); l.setRole(role);
        return lecturerRepository.save(l);
    }

    private List<StudentClass> createStudentClasses() {
        List<StudentClass> classes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            classes.add(createStudentClass("63TH" + i, "63TH" + i, "K63"));
            classes.add(createStudentClass("62KT" + i, "62KT" + i, "K62"));
        }
        return classes;
    }
    private StudentClass createStudentClass(String code, String name, String semester) {
        StudentClass sc = new StudentClass();
        sc.setClassCode(code); sc.setClassName(name); sc.setSemester(semester);
        return studentClassRepository.save(sc);
    }

    private void createStudents(List<StudentClass> studentClasses) {
        for (StudentClass sc : studentClasses) {
            for (int i = 0; i < 30; i++) {
                createStudent(faker.name().fullName(), "SV" + sc.getClassCode() + String.format("%02d", i), sc);
            }
        }
    }
    private void createStudent(String fullName, String code, StudentClass sc) {
        Student s = new Student();
        s.setFullName(fullName); s.setStudentCode(code); s.setStudentClass(sc);
        studentRepository.save(s);
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
    private Subject createSubject(String name, String code, int credits, Department dept) {
        Subject s = new Subject();
        s.setSubjectName(name); s.setSubjectCode(code); s.setCredits(credits); s.setDepartment(dept);
        return subjectRepository.save(s);
    }

    private List<Assignment> createAssignments(List<Lecturer> lecturers, List<Subject> subjects, List<StudentClass> studentClasses) {
        List<Assignment> assignments = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            assignments.add(createAssignment(lecturers.get(random.nextInt(lecturers.size())), subjects.get(random.nextInt(subjects.size())), studentClasses.get(random.nextInt(studentClasses.size())), 15, 30));
        }
        return assignments;
    }
    private Assignment createAssignment(Lecturer l, Subject s, StudentClass sc, int th, int pr) {
        Assignment a = new Assignment();
        a.setLecturer(l); a.setSubject(s); a.setStudentClass(sc);
        a.setTheorySession(th); a.setPracticeSession(pr);
        return assignmentRepository.save(a);
    }

    private void createSchedulesForCurrentMonth(List<Assignment> assignments) {
        if (assignments.isEmpty()) return;
        Random random = new Random();
        YearMonth currentMonth = YearMonth.now();

        for (LocalDate date = currentMonth.atDay(1); date.isBefore(currentMonth.atEndOfMonth().plusDays(1)); date = date.plusDays(1)) {
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) continue;
            int schedulesPerDay = 2 + random.nextInt(3);
            for (int i = 0; i < schedulesPerDay; i++) {
                Assignment assignment = assignments.get(random.nextInt(assignments.size()));
                int lessonOrder = i + 1;
                int startPeriod = (i % 2 == 0) ? 1 : 7;
                Schedule schedule = createSchedule(assignment, date.atTime(startPeriod == 1 ? 7 : 13, 0), lessonOrder, startPeriod, startPeriod + 2, "C1-" + (301 + random.nextInt(10)));
                if (schedule.getSessionDate().isBefore(LocalDateTime.now())) {
                    List<Student> students = studentRepository.findByStudentClass(assignment.getStudentClass());
                    if (!students.isEmpty()) {
                        schedule.setStatus(ScheduleStatus.TAUGHT);
                        scheduleRepository.save(schedule);
                        for (Student student : students) {
                            createAttendance(schedule, student, random.nextInt(100) < 90);
                        }
                    }
                }
            }
        }
    }
    private Schedule createSchedule(Assignment a, LocalDateTime dt, int order, int start, int end, String room) {
        Schedule s = new Schedule();
        s.setAssignment(a); s.setSessionDate(dt); s.setLessonOrder(order);
        s.setStartPeriod(start); s.setEndPeriod(end); s.setClassroom(room);
        s.setStatus(ScheduleStatus.NOT_TAUGHT);
        return scheduleRepository.save(s);
    }

    private void createAttendance(Schedule schedule, Student student, boolean isPresent) {
        Attendance a = new Attendance();
        a.setSchedule(schedule); a.setStudent(student); a.setIsPresent(isPresent);
        a.setTimestamp(schedule.getSessionDate());
        attendanceRepository.save(a);
    }
}
