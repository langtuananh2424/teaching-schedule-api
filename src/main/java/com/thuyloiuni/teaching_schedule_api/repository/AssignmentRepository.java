package com.thuyloiuni.teaching_schedule_api.repository;

import com.thuyloiuni.teaching_schedule_api.entity.Assignment;
import com.thuyloiuni.teaching_schedule_api.entity.Lecturer;
import com.thuyloiuni.teaching_schedule_api.entity.StudentClass;
import com.thuyloiuni.teaching_schedule_api.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

    List<Assignment> findByLecturer_LecturerId(Integer lecturerId);

    // --- New Queries for Report Filtering ---

    @Query("SELECT DISTINCT a.subject FROM Assignment a WHERE a.semester.id = :semesterId")
    List<Subject> findSubjectsBySemesterId(@Param("semesterId") Integer semesterId);

    @Query("SELECT DISTINCT a.lecturer FROM Assignment a WHERE a.semester.id = :semesterId AND a.subject.subjectId = :subjectId")
    List<Lecturer> findLecturersBySemesterAndSubject(@Param("semesterId") Integer semesterId, @Param("subjectId") Integer subjectId);

    @Query("SELECT a.studentClass FROM Assignment a WHERE a.semester.id = :semesterId AND a.subject.subjectId = :subjectId AND a.lecturer.lecturerId = :lecturerId")
    List<StudentClass> findStudentClassesBySemesterAndSubjectAndLecturer(@Param("semesterId") Integer semesterId, @Param("subjectId") Integer subjectId, @Param("lecturerId") Integer lecturerId);

    // --- Query for fetching the specific assignment for a report (FIXED) ---
    Optional<Assignment> findBySemester_IdAndSubject_SubjectIdAndLecturer_LecturerIdAndStudentClass_ClassId(
            Integer semesterId, Integer subjectId, Integer lecturerId, Integer classId);

}
