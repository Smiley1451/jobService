package com.prohands.job.service;

import com.prohands.job.dto.request.JobRequest;
import com.prohands.job.dto.response.JobResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JobService {



    Mono<JobResponse> createJob(JobRequest request);
    Mono<JobResponse> getJobById(UUID id);
    Flux<JobResponse> getAllJobs();
    Flux<JobResponse> getJobsByProvider(String providerId);
    Mono<JobResponse> updateJob(UUID jobId, JobRequest request);
    Mono<Void> updateJobStatus(UUID jobId, String status);
    Mono<Void> deleteJob(UUID jobId);
    Flux<JobResponse> getJobFeed(double lat, double lon, int page, int size);
}