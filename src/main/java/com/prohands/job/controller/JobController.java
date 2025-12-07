package com.prohands.job.controller;

import com.prohands.job.dto.request.JobRequest;
import com.prohands.job.dto.response.JobResponse;
import com.prohands.job.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<JobResponse> createJob(@RequestBody @Valid JobRequest request) {
        return jobService.createJob(request);
    }

    @GetMapping("/{jobId}")
    public Mono<JobResponse> getJob(@PathVariable UUID jobId) {
        return jobService.getJobById(jobId);
    }

    @GetMapping
    public Flux<JobResponse> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/provider/{providerId}")
    public Flux<JobResponse> getJobsByProvider(@PathVariable String providerId) {
        return jobService.getJobsByProvider(providerId);
    }

    @GetMapping("/feed")
    public Flux<JobResponse> getJobFeed(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return jobService.getJobFeed(latitude, longitude, page, size);
    }

    @PutMapping("/{jobId}")
    public Mono<JobResponse> updateJob(@PathVariable UUID jobId, @RequestBody @Valid JobRequest request) {
        return jobService.updateJob(jobId, request);
    }

    @PatchMapping("/{jobId}/status")
    public Mono<Void> updateStatus(@PathVariable UUID jobId, @RequestParam String status) {
        return jobService.updateJobStatus(jobId, status);
    }

    @DeleteMapping("/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteJob(@PathVariable UUID jobId) {
        return jobService.deleteJob(jobId);
    }
}