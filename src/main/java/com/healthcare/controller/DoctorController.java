package com.healthcare.controller;

import com.healthcare.dto.ApiResponse;
import com.healthcare.dto.DoctorProfileRequest;
import com.healthcare.entity.Doctor;
import com.healthcare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllDoctors() {
        return ResponseEntity.ok(ApiResponse.success("Doctors retrieved",
                doctorService.getAllDoctors()));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAvailableDoctors() {
        return ResponseEntity.ok(ApiResponse.success("Available doctors",
                doctorService.getAvailableDoctors()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Doctor retrieved",
                doctorService.getDoctorById(id)));
    }

    @PutMapping("/availability")
    public ResponseEntity<ApiResponse<Doctor>> updateAvailability(
            @RequestParam String status,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Availability updated",
                doctorService.updateAvailability(auth.getName(), status)));
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<Doctor>> createProfile(
            @RequestBody DoctorProfileRequest request,
            Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Doctor profile created",
                doctorService.createProfile(request, auth.getName())));
    }
}