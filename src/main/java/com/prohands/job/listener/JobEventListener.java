package com.prohands.job.listener;

import com.prohands.job.dto.request.JobRequest;
import com.prohands.job.dto.event.JobUpdateEvent;
import com.prohands.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobEventListener {

    private final JobService jobService;


    @KafkaListener(
            topics = "job-create-requests",
            groupId = "job-creation-group",
            containerFactory = "jobRequestContainerFactory"
    )
    public void handleJobCreationRequest(ConsumerRecord<String, JobRequest> record) {
        JobRequest request = record.value();
        if (request == null) return;

        log.info("Listener: Received Create Request: {}", request.title());

        try {
            jobService.processJobRequest(request)
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.error("Listener: Failed to create job", e);
            throw e;
        }
    }


    @KafkaListener(
            topics = "job-updates",
            groupId = "job-service-group",
            containerFactory = "jobUpdateContainerFactory"
    )
    public void handleJobUpdates(ConsumerRecord<String, JobUpdateEvent> record) {
        JobUpdateEvent event = record.value();
        if (event == null) return;

        log.info("Listener: Received Update Event: ID={}, Status={}", event.jobId(), event.status());

        try {
            jobService.updateJobStatus(event.jobId(), event.status())
                    .block(Duration.ofSeconds(10));
        } catch (Exception e) {
            log.error("Listener: Failed to update status", e);
            throw e;
        }
    }
}