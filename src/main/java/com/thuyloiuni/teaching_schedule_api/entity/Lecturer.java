package com.thuyloiuni.teaching_schedule_api.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "department_id", nullable = false)
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Assignment> assignments;
}
