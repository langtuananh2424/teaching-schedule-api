package com.thuyloiuni.teaching_schedule_api.entity;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", referencedColumnName = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "lesson_order", nullable = false)
    private Integer lessonOrder;

    @Column(name = "start_period", nullable = false)
    private Integer startPeriod;

    @Column(name = "end_period", nullable = false)
    private Integer endPeriod;

    @Column(name = "classroom", nullable = false, length = 20)
    private String classroom;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ScheduleStatus status;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<Attendance> attendances;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, fetch= FetchType.LAZY)
    private Set<AbsenceRequest> absenceRequests;

}
