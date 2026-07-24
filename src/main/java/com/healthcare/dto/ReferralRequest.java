package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReferralRequest {
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    @NotBlank(message = "Source hospital is required")
    private String sourceHospital;
    @NotBlank(message = "Destination hospital is required")
    private String destinationHospital;
    @NotBlank(message = "Referral reason is required")
    private String referralReason;
    private String urgencyLevel;
    private String notes;
}
