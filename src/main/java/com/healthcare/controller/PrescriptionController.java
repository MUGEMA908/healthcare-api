package com.healthcare.controller;

import com.healthcare.dto.ApiResponse;
import com.healthcare.dto.PrescriptionRequest;
import com.healthcare.entity.Prescription;
import com.healthcare.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    // Doctor: issue prescription
    @PostMapping
    public ResponseEntity<ApiResponse<Prescription>> prescribe(
            @Valid @RequestBody PrescriptionRequest request,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Prescription issued",
                prescriptionService.prescribe(request, auth.getName())));
    }

    // Patient: get own prescriptions
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Prescription>>> myPrescriptions(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Prescriptions retrieved",
                prescriptionService.getMyPrescriptions(auth.getName())));
    }

    // Doctor/Admin: get prescriptions by patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Prescription>>> patientPrescriptions(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.success("Prescriptions retrieved",
                prescriptionService.getPatientPrescriptions(patientId)));
    }
}
