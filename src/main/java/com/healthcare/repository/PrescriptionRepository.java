package com.healthcare.repository;

import com.healthcare.entity.Patient;
import com.healthcare.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatient(Patient patient);
    List<Prescription> findByPatientAndStatus(Patient patient, Prescription.PrescriptionStatus status);
}
