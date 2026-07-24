package com.healthcare.repository;

import com.healthcare.entity.Patient;
import com.healthcare.entity.PatientHealthUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatientHealthUpdateRepository extends JpaRepository<PatientHealthUpdate, Long> {
    List<PatientHealthUpdate> findByPatientOrderByReportedAtDesc(Patient patient);
}
