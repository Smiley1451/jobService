package com.prohands.job.config;

import java.math.BigDecimal;
import java.util.List;


public record JobCreatedEvent(

        String providerId,
        String title,
        String description,
        BigDecimal wage,
        Double latitude,
        Double longitude,
        List<String> requiredSkills,
        Integer numberOfEmployees
) {

}