package com.prohands.job.service.impl;

import com.prohands.job.config.JobCreatedEvent;
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
    public Mono<JobResponse> createJob(JobRequest request) {
        log.info("Client creating job: {}", request.title());

        Job job = new Job();
        job.setProviderId(request.providerId());
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setWage(request.wage());
        job.setLatitude(request.latitude());
        job.setLongitude(request.longitude());
        job.setNumberOfEmployees(request.numberOfEmployees() != null ? request.numberOfEmployees() : 1);
        job.setStatus("OPEN");
        job.setSource("DIRECT_API");
        job.setCreatedAt(Instant.now());
        job.setUpdatedAt(Instant.now());

        if (request.requiredSkills() != null) {
            job.setRequiredSkills(request.requiredSkills().toArray(new String[0]));
        }

        return jobRepository.save(job)
                .flatMap(savedJob -> {
                    List<String> skillsList = savedJob.getRequiredSkills() != null ? Arrays.asList(savedJob.getRequiredSkills()) : List.of();

                    JobCreatedEvent event = new JobCreatedEvent(
                            savedJob.getProviderId(),
                            savedJob.getTitle(),
                            savedJob.getDescription(),
                            savedJob.getWage(),
                            savedJob.getLatitude(),
                            savedJob.getLongitude(),
                            skillsList,
                            savedJob.getNumberOfEmployees()
                    );

                    return Mono.fromFuture(kafkaTemplate.send("job-create-requests", event))
                            .doOnError(e -> log.error("Failed to publish JobCreatedEvent", e))
                            .thenReturn(mapToResponse(savedJob));
                });
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
    public Flux<JobResponse> getJobsByProvider(String providerId) {
        return jobRepository.findByProviderId(providerId)
                .map(this::mapToResponse);
    }

    @Override
    public Mono<JobResponse> updateJob(UUID jobId, JobRequest request) {
        return jobRepository.findById(jobId)
                .flatMap(job -> {
                    job.setTitle(request.title());
                    job.setDescription(request.description());
                    job.setWage(request.wage());
                    job.setLatitude(request.latitude());
                    job.setLongitude(request.longitude());
                    job.setNumberOfEmployees(request.numberOfEmployees());
                    if (request.requiredSkills() != null) {
                        job.setRequiredSkills(request.requiredSkills().toArray(new String[0]));
                    }
                    job.setUpdatedAt(Instant.now());
                    return jobRepository.save(job);
                })
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Job not found for update")));
    }

    @Override
    public Mono<Void> updateJobStatus(UUID jobId, String status) {
        return jobRepository.findById(jobId)
                .flatMap(job -> {
                    job.setStatus(status);
                    job.setUpdatedAt(Instant.now());
                    return jobRepository.save(job);
                })
                .doOnSuccess(j -> log.info("Job {} status updated to {}", jobId, status))
                .then();
    }

    @Override
    public Flux<JobResponse> getJobFeed(double lat, double lon, int page, int size) {

        long offset = (long) page * size;

        log.debug("Fetching job feed for lat: {}, lon: {}, page: {}", lat, lon, page);

        return jobRepository.findNearbyJobs(lat, lon, size, offset)
                .map(this::mapToResponse);
    }


    @Override
    public Mono<Void> deleteJob(UUID jobId) {
        return jobRepository.findById(jobId)
                .flatMap(jobRepository::delete)
                .doOnSuccess(v -> log.info("Job {} deleted", jobId));
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