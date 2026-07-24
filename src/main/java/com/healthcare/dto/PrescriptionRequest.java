package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrescriptionRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    private Long appointmentId;
    @NotBlank(message = "Drug name is required")
    private String drugName;
    @NotBlank(message = "Dosage is required")
    private String dosage;
    @NotBlank(message = "Frequency is required")
    private String frequency;
    private String duration;
    private String instructions;
}
