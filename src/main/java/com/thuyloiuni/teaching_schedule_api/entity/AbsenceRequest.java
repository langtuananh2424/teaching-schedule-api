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
@Table(name = "absence_requests") // Corrected table name
public class AbsenceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", unique = true)
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id", nullable = false)
    private Lecturer lecturer;

    @Lob
    @Column(name = "reason", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "department_approval", nullable = false)
    private ApprovalStatus departmentApproval;

    @Enumerated(EnumType.STRING)
    @Column(name = "ctsv_approval", nullable = false) // CTSV stands for "Công tác Sinh viên"
    private ApprovalStatus ctsvApproval;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // The 'approver' field is removed as there are now multiple approval levels.
    // You can add separate fields for department_approver_id and ctsv_approver_id if needed.

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
