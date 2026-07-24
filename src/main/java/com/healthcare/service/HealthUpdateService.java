package com.healthcare.service;

import com.healthcare.dto.HealthUpdateRequest;
import com.healthcare.entity.*;
import com.healthcare.exception.ApiException;
import com.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthUpdateService {

    private final PatientHealthUpdateRepository healthUpdateRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    public PatientHealthUpdate submitUpdate(HealthUpdateRequest request, String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ApiException("User not found"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Patient profile not found"));

        PatientHealthUpdate update = new PatientHealthUpdate();
        update.setPatient(patient);
        update.setSymptoms(request.getSymptoms());
        update.setSideEffects(request.getSideEffects());
        update.setGeneralFeeling(request.getGeneralFeeling());
        update.setPainLevel(request.getPainLevel());
        update.setAdditionalNotes(request.getAdditionalNotes());

        if (request.getPrescriptionId() != null) {
            prescriptionRepository.findById(request.getPrescriptionId())
                    .ifPresent(update::setPrescription);
        }

        return healthUpdateRepository.save(update);
    }

    public List<PatientHealthUpdate> getPatientUpdates(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ApiException("Patient not found"));
        return healthUpdateRepository.findByPatientOrderByReportedAtDesc(patient);
    }

    public List<PatientHealthUpdate> getMyUpdates(String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ApiException("User not found"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Patient profile not found"));
        return healthUpdateRepository.findByPatientOrderByReportedAtDesc(patient);
    }
}
