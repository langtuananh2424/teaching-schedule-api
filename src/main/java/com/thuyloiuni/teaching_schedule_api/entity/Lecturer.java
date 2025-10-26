package com.thuyloiuni.teaching_schedule_api.entity;

import com.thuyloiuni.teaching_schedule_api.entity.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

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

    @Column(name = "lecturer_code", unique = true, nullable = false, length = 20)
    private String lecturerCode;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "department_id", nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private RoleType role;

    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Assignment> assignments;

    // The following relationships have been removed because the 'approver' field
    // no longer exists in AbsenceRequest and MakeupSession entities due to the new
    // two-level approval logic. If you need to track who approved at each level,
    // you should add specific fields like 'departmentApprover' and 'ctsvApprover'
    // to those entities.

}
