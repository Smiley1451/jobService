package com.prohands.job.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record JobResponse(
        UUID jobId,
        UUID providerId,
        String title,
        String description,
        BigDecimal wage,
        String status,
        Double latitude,
        Double longitude,
        List<String> requiredSkills,
        Integer numberOfEmployees,
        Instant createdAt

) {}