package com.healthcare.dto;

import lombok.Data;

@Data
public class DoctorProfileRequest {
    private String specialization;
    private String licenseNumber;
    private String department;
    private String qualifications;
    private String workingHours;
}