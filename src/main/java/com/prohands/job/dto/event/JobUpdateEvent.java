package com.prohands.job.dto.event;

import java.util.UUID;

public record JobUpdateEvent(
        UUID jobId,
        String status
) {}