package com.prohands.job.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record JobRequest(
        @NotNull String providerId,
        @NotBlank String title,
        String description,
        @Min(1) BigDecimal wage,
        @NotNull Double latitude,
        @NotNull Double longitude,
        List<String> requiredSkills,
        @Min(value = 1, message = "At least one employee is required")
        Integer numberOfEmployees
) {}