package com.thuyloiuni.teaching_schedule_api.entity;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

import org.hibernate.sql.ast.tree.update.Assignment;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lecturers")
public class Lecturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecturer_id")
    private Integer lecturerId;

    @Column(name = "lecture_code", unique = true, nullable = false, length = 20)
    private String lectureCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable=false, length=255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName= "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private RoleType role;

    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Assignment> assignments;

    @OneToMany(mappedBy= "approver", fetch= FetchType.LAZY)
    private Set<AbsenceRequest> approvedAbsenceRequests;

    @OneToMany(mappedBy = "approver", fetch = FetchType.LAZY)
    private Set<MakeupSession> approvedMakeupSessions;
}
