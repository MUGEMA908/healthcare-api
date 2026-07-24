package com.healthcare.service;

import com.healthcare.dto.DoctorProfileRequest;
import com.healthcare.entity.Doctor;
import com.healthcare.exception.ApiException;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByAvailabilityStatus(Doctor.AvailabilityStatus.AVAILABLE);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ApiException("Doctor not found"));
    }

    public Doctor updateAvailability(String email, String status) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));
        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ApiException("Doctor profile not found"));
        try {
            doctor.setAvailabilityStatus(Doctor.AvailabilityStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid status. Use: AVAILABLE, BUSY, ON_LEAVE, OFF_DUTY");
        }
        return doctorRepository.save(doctor);
    }

    public Doctor createProfile(DoctorProfileRequest request, String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found"));

        if (doctorRepository.findByUserId(user.getId()).isPresent()) {
            throw new ApiException("Doctor profile already exists");
        }

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setDepartment(request.getDepartment());
        doctor.setQualifications(request.getQualifications());
        doctor.setWorkingHours(request.getWorkingHours());
        return doctorRepository.save(doctor);
    }
}