package com.healthcare.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_health_updates")
@Data
public class PatientHealthUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    private String symptoms;
    private String sideEffects;
    private String generalFeeling; // e.g. "Better", "Worse", "Same"
    private Integer painLevel; // 1-10
    private String additionalNotes;
    private LocalDateTime reportedAt = LocalDateTime.now();
}
