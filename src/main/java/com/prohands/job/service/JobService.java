package com.prohands.job.service;

import com.prohands.job.dto.request.JobRequest;
import com.prohands.job.dto.response.JobResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JobService {

    // 1. PRODUCER: Sends request to Kafka (Returns "Request Queued")
    // Note: Returns String because the Job ID doesn't exist yet!
    Mono<String> createJob(JobRequest request);

    // 2. WORKER: Actually saves to DB (Called by Kafka Listener)
    Mono<JobResponse> processJobRequest(JobRequest request);

    // 3. READ Operations
    Mono<JobResponse> getJobById(UUID id);
    Flux<JobResponse> getAllJobs();

    // 4. UPDATE Operations (Status changes)
    Mono<Void> updateJobStatus(UUID jobId, String status);
}