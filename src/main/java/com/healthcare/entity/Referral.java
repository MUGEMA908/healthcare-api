package com.healthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Data
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "referring_doctor_id", nullable = false)
    private Doctor referringDoctor;

    @Column(nullable = false)
    private String sourceHospital;

    @Column(nullable = false)
    private String destinationHospital;

    @Column(nullable = false)
    private String referralReason;

    private String urgencyLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Enumerated(EnumType.STRING)
    private ReferralStatus status = ReferralStatus.PENDING;

    private String notes;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    public enum ReferralStatus {
        PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, REJECTED
    }
}
