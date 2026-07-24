package com.healthcare.config;

import com.healthcare.entity.Doctor;
import com.healthcare.entity.Patient;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.PatientRepository;
import com.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("Admin User",        "admin@healthcare.com",        "admin@123",       "0780000001", User.Role.ADMIN);
        User doctorUser = seedUser("Dr. Jean Bosco",  "doctor@healthcare.com",       "doctor@123",      "0780000002", User.Role.DOCTOR);
        User patientUser = seedUser("Marie Claire",   "patient@healthcare.com",      "patient@123",     "0780000003", User.Role.PATIENT);
        seedUser("Nurse Alice",       "nurse@healthcare.com",        "nurse@123",       "0780000004", User.Role.NURSE);
        seedUser("Pharmacist Bob",    "pharmacist@healthcare.com",   "pharmacist@123",  "0780000005", User.Role.PHARMACIST);

        if (doctorUser != null && doctorRepository.findByUserId(doctorUser.getId()).isEmpty()) {
            Doctor d = new Doctor();
            d.setUser(doctorUser);
            d.setSpecialization("General Medicine");
            d.setLicenseNumber("RW-DOC-001");
            d.setDepartment("Outpatient");
            d.setQualifications("MBBCh, University of Rwanda");
            d.setWorkingHours("Mon-Fri 08:00-17:00");
            doctorRepository.save(d);
        }

        if (patientUser != null && patientRepository.findByUserId(patientUser.getId()).isEmpty()) {
            Patient p = new Patient();
            p.setUser(patientUser);
            p.setDateOfBirth(LocalDate.of(1990, 5, 15));
            p.setGender("Female");
            p.setBloodGroup("O+");
            p.setAddress("Kigali, Rwanda");
            p.setEmergencyContactName("Jean Pierre");
            p.setEmergencyContactPhone("0780000099");
            p.setNationalId("1199080123456789");
            patientRepository.save(p);
        }

        System.out.println("\n================================================");
        System.out.println("   SMART HEALTHCARE SYSTEM - SERVER RUNNING");
        System.out.println("   Base URL : http://localhost:8080");
        System.out.println("------------------------------------------------");
        System.out.println("   admin@healthcare.com      / admin@123");
        System.out.println("   doctor@healthcare.com     / doctor@123");
        System.out.println("   patient@healthcare.com    / patient@123");
        System.out.println("   nurse@healthcare.com      / nurse@123");
        System.out.println("   pharmacist@healthcare.com / pharmacist@123");
        System.out.println("================================================\n");
    }

    private User seedUser(String fullName, String email, String pwd, String phone, User.Role role) {
        if (userRepository.existsByEmail(email)) return userRepository.findByEmail(email).orElse(null);
        User u = new User();
        u.setFullName(fullName); u.setEmail(email);
        u.setPassword(passwordEncoder.encode(pwd));
        u.setPhone(phone); u.setRole(role); u.setEnabled(true);
        return userRepository.save(u);
    }
}
