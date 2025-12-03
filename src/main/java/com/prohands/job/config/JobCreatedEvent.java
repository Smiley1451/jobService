package com.prohands.job.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public record JobCreatedEvent(

        UUID providerId,
        String title,
        String description,
        BigDecimal wage,
        Double latitude,
        Double longitude,
        List<String> requiredSkills,
        Integer numberOfEmployees
) {

}