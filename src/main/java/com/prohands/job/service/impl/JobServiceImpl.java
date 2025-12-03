package com.prohands.job.service.impl;

import com.prohands.job.dto.request.JobRequest;
import com.prohands.job.dto.response.JobResponse;
import com.prohands.job.entity.Job;
import com.prohands.job.repository.JobRepository;
import com.prohands.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public Mono<String> createJob(JobRequest request) {

        log.info("Received Creation Request. Sending to Kafka...");

        return Mono.fromFuture(kafkaTemplate.send("job-create-requests", request))
                .doOnSuccess(res -> log.info("Request pushed to Kafka: {}", request.title()))
                .doOnError(err -> log.error("Kafka Send Failed", err))
                .thenReturn("Job creation request accepted. Processing in background.");
    }


    @Override
    public Mono<JobResponse> processJobRequest(JobRequest request) {
        log.info("Worker: Saving Job to DB for Provider: {}", request.providerId());

        Job job = new Job();
        job.setProviderId(request.providerId());
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setWage(request.wage());
        job.setLatitude(request.latitude());
        job.setLongitude(request.longitude());
        job.setNumberOfEmployees(request.numberOfEmployees() != null ? request.numberOfEmployees() : 1);

        job.setStatus("OPEN");
        job.setSource("KAFKA_ASYNC");
        job.setCreatedAt(Instant.now());
        job.setUpdatedAt(Instant.now());

        if (request.requiredSkills() != null) {
            job.setRequiredSkills(request.requiredSkills().toArray(new String[0]));
        }


        return jobRepository.save(job)
                .map(this::mapToResponse)
                .doOnSuccess(saved -> log.info("Worker: Job Created! Generated ID: {}", saved.jobId()));
    }


    @Override
    public Mono<JobResponse> getJobById(UUID id) {
        return jobRepository.findById(id)
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Job not found: " + id)));
    }

    @Override
    public Flux<JobResponse> getAllJobs() {
        return jobRepository.findAll().map(this::mapToResponse);
    }

    @Override
    public Mono<Void> updateJobStatus(UUID jobId, String status) {
        return jobRepository.findById(jobId)
                .flatMap(job -> {
                    job.setStatus(status);
                    job.setUpdatedAt(Instant.now());
                    return jobRepository.save(job);
                }).then();
    }


    private JobResponse mapToResponse(Job job) {
        List<String> skills = job.getRequiredSkills() != null ?
                Arrays.asList(job.getRequiredSkills()) : List.of();

        return new JobResponse(
                job.getId(),
                job.getProviderId(),
                job.getTitle(),
                job.getDescription(),
                job.getWage(),
                job.getStatus(),
                job.getLatitude(),
                job.getLongitude(),
                skills,


                job.getNumberOfEmployees(),

                job.getCreatedAt()
        );
    }
}