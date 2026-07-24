package com.healthcare.dto;

import lombok.Data;

@Data
public class HealthUpdateRequest {
    private Long prescriptionId;
    private String symptoms;
    private String sideEffects;
    private String generalFeeling;
    private Integer painLevel;
    private String additionalNotes;
}
