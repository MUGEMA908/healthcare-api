package com.healthcare.controller;

import com.healthcare.dto.ApiResponse;
import com.healthcare.dto.AppointmentRequest;
import com.healthcare.entity.Appointment;
import com.healthcare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // NURSE only: book appointment for a patient
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<Appointment>> book(
            @Valid @RequestBody AppointmentRequest request,
            Authentication auth) {
        Appointment appointment = appointmentService.bookAppointment(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Appointment booked successfully", appointment));
    }

    // PATIENT: view own appointments
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Appointment>>> myAppointments(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Appointments retrieved",
                appointmentService.getMyAppointments(auth.getName())));
    }

    // DOCTOR: view own appointments
    @GetMapping("/doctor")
    public ResponseEntity<ApiResponse<List<Appointment>>> doctorAppointments(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.success("Appointments retrieved",
                appointmentService.getDoctorAppointments(auth.getName())));
    }

    // NURSE/ADMIN: view all appointments
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Appointment>>> allAppointments() {
        return ResponseEntity.ok(ApiResponse.success("All appointments retrieved",
                appointmentService.getAllAppointments()));
    }

    // DOCTOR/ADMIN: update appointment status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Appointment>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                appointmentService.updateStatus(id, status)));
    }

    // PUBLIC: verify QR token — hospital reception scans this
    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse<Appointment>> verifyToken(@PathVariable String token) {
        return ResponseEntity.ok(ApiResponse.success("Valid appointment",
                appointmentService.verifyQrToken(token)));
    }
}