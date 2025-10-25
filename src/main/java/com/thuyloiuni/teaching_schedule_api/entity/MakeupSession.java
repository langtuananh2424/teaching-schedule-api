package com.thuyloiuni.teaching_schedule_api.entity;

import com.thuyloiuni.teaching_schedule_api.model.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "makeup_sessions") // Corrected table name
public class MakeupSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "makeup_session_id")
    private Integer makeupSessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "absent_session_id", nullable = false)
    private Schedule absentSchedule; // Renamed for clarity

    @Column(name = "makeup_date", nullable = false)
    private LocalDateTime makeupDate;

    @Column(name = "makeup_start_period", nullable = false)
    private Integer makeupStartPeriod;

    @Column(name = "makeup_end_period", nullable = false)
    private Integer makeupEndPeriod;

    @Column(name = "makeup_classroom", nullable = false, length = 20)
    private String makeupClassroom;

    @Enumerated(EnumType.STRING)
    @Column(name = "department_approval", nullable = false)
    private ApprovalStatus departmentApproval;

    @Enumerated(EnumType.STRING)
    @Column(name = "ctsv_approval", nullable = false)
    private ApprovalStatus ctsvApproval;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Set default approval statuses on creation
        if (departmentApproval == null) {
            departmentApproval = ApprovalStatus.PENDING;
        }
        if (ctsvApproval == null) {
            ctsvApproval = ApprovalStatus.PENDING;
        }
    }
}
