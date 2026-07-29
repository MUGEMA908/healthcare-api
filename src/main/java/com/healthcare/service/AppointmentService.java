package com.healthcare.service;

import com.healthcare.dto.AppointmentRequest;
import com.healthcare.entity.*;
import com.healthcare.exception.ApiException;
import com.healthcare.exception.ResourceNotFoundException;
import com.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // Only NURSE or ADMIN can book appointment for a patient
    public Appointment bookAppointment(AppointmentRequest request, String nurseEmail) {
        User nurseUser = userRepository.findByEmail(nurseEmail)
                .orElseThrow(() -> new ApiException("User not found"));

        if (nurseUser.getRole() != User.Role.NURSE && nurseUser.getRole() != User.Role.ADMIN) {
            throw new ApiException("Only nurses or admins can book appointments for patients.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.getPatientId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", request.getDoctorId()));

        if (doctor.getAvailabilityStatus() == Doctor.AvailabilityStatus.ON_LEAVE ||
            doctor.getAvailabilityStatus() == Doctor.AvailabilityStatus.OFF_DUTY) {
            throw new ApiException("Doctor is currently unavailable.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());
        appointment.setQrToken(UUID.randomUUID().toString());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        // Notify patient
        sendNotification(
            patient.getUser(),
            "Appointment Booked",
            "Nurse " + nurseUser.getFullName() + " has booked an appointment for you with Dr. " +
            doctor.getUser().getFullName() + " on " + request.getAppointmentDate() +
            " at " + request.getAppointmentTime() +
            ". Your QR Token: " + saved.getQrToken(),
            Notification.NotificationType.APPOINTMENT_CONFIRMED
        );

        // Notify doctor
        sendNotification(
            doctor.getUser(),
            "New Appointment",
            "Patient " + patient.getUser().getFullName() +
            " has been booked for " + request.getAppointmentDate() +
            " at " + request.getAppointmentTime() +
            ". Reason: " + request.getReason(),
            Notification.NotificationType.APPOINTMENT_CONFIRMED
        );

        return saved;
    }

    // Patient views own appointments
    public List<Appointment> getMyAppointments(String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ApiException("User not found"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Patient profile not found"));
        return appointmentRepository.findByPatient(patient);
    }

    // Doctor views own appointments
    public List<Appointment> getDoctorAppointments(String doctorEmail) {
        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ApiException("User not found"));
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Doctor profile not found"));
        return appointmentRepository.findByDoctor(doctor);
    }

    // Nurse views all appointments they manage
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Doctor or Admin updates appointment status
    public Appointment updateStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        try {
            appointment.setStatus(Appointment.AppointmentStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid status. Use: PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW");
        }

        // Notify patient of status change
        sendNotification(
            appointment.getPatient().getUser(),
            "Appointment " + status,
            "Your appointment on " + appointment.getAppointmentDate() +
            " status has been updated to: " + status,
            Notification.NotificationType.APPOINTMENT_CONFIRMED
        );

        return appointmentRepository.save(appointment);
    }

    // Public: verify QR token (hospital reception scans)
    public Appointment verifyQrToken(String token) {
        return appointmentRepository.findByQrToken(token)
                .orElseThrow(() -> new ApiException("Invalid or expired appointment token."));
    }

    private void sendNotification(User user, String title, String message,
                                   Notification.NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);
    }
}