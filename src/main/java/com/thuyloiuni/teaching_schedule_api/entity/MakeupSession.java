package com.thuyloiuni.teaching_schedule_api.entity;

import java.time.LocalDateTime;

import com.thuyloiuni.teaching_schedule_api.entity.enums.ApprovalStatus;

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
import jakarta.persistence.OneToOne;
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
@Table(name = "makeupsessions")
public class MakeupSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "makeup_session_id")
    private Integer makeupSessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "absent_session_id", nullable = false)
    private Schedule absentRequest;

    @Column(name = "makeup_date", nullable = false)
    private LocalDateTime makeupDate;

    @Column(name = "makeup_start_period", nullable = false)
    private Integer makeupStartPeriod;

    @Column(name = "makeup_end_period", nullable = false)
    private Integer makeupEndPeriod;

    @Column(name = "makeup_classroom", nullable = false, length=20)
    private String makeupClassroom;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length=50)
    private ApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Lecturer approver;
}
