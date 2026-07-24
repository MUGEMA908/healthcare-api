package com.healthcare.controller;

import com.healthcare.dto.ApiResponse;
import com.healthcare.dto.HealthUpdateRequest;
import com.healthcare.entity.PatientHealthUpdate;
import com.healthcare.service.HealthUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-updates")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HealthUpdateController {

    private final HealthUpdateService healthUpdateService;

    // Patient: submit health update
    @PostMapping
    public ResponseEntity<ApiResponse<PatientHealthUpdate>> submit(
            @RequestBody HealthUpdateRequest request,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Health update submitted",
                healthUpdateService.submitUpdate(request, auth.getName())));
    }

    // Patient: view own updates
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<PatientHealthUpdate>>> myUpdates(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Updates retrieved",
                healthUpdateService.getMyUpdates(auth.getName())));
    }

    // Doctor: view updates for specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PatientHealthUpdate>>> patientUpdates(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.success("Updates retrieved",
                healthUpdateService.getPatientUpdates(patientId)));
    }
}
