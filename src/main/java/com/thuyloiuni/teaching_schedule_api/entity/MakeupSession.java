package com.thuyloiuni.teaching_schedule_api.entity;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "makeup_sessions")
public class MakeupSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "makeup_session_id")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "absent_session_id", referencedColumnName = "session_id")
    private Schedule absentSchedule;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "makeup_schedule_id", referencedColumnName = "session_id", unique = true)
    private Schedule makeupSchedule;

    @Column(name = "makeup_date", nullable = false)
    private LocalDateTime makeupDate;

    @Column(name = "makeup_start_period", nullable = false)
    private int makeupStartPeriod;

    @Column(name = "makeup_end_period", nullable = false)
    private int makeupEndPeriod;

    @Column(name = "makeup_classroom", nullable = false)
    private String makeupClassroom;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_approval", nullable = false)
    private ApprovalStatus managerApproval = ApprovalStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_affairs_approval", nullable = false)
    private ApprovalStatus academicAffairsApproval = ApprovalStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
