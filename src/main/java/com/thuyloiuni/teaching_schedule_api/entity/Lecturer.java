package com.thuyloiuni.teaching_schedule_api.entity;

import java.util.Set;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
    private String lecturerCode;

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
