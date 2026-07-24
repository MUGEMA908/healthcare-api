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

    public Appointment bookAppointment(AppointmentRequest request, String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ApiException("User not found"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Patient profile not found. Complete your profile first."));
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
        appointment.setQrToken(UUID.randomUUID().toString()); // anti-resell unique token
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        // Notify patient
        sendNotification(user, "Appointment Booked",
                "Your appointment with " + doctor.getUser().getFullName() +
                " on " + request.getAppointmentDate() + " at " + request.getAppointmentTime() +
                " has been booked. Token: " + saved.getQrToken(),
                Notification.NotificationType.APPOINTMENT_CONFIRMED);

        return saved;
    }

    public List<Appointment> getMyAppointments(String patientEmail) {
        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new ApiException("User not found"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Patient profile not found"));
        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> getDoctorAppointments(String doctorEmail) {
        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new ApiException("User not found"));
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Doctor profile not found"));
        return appointmentRepository.findByDoctor(doctor);
    }

    public Appointment updateStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        try {
            appointment.setStatus(Appointment.AppointmentStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid status. Use: PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW");
        }
        return appointmentRepository.save(appointment);
    }

    public Appointment verifyQrToken(String token) {
        return appointmentRepository.findByQrToken(token)
                .orElseThrow(() -> new ApiException("Invalid or expired appointment token."));
    }

    private void sendNotification(User user, String title, String message, Notification.NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);
    }
}
