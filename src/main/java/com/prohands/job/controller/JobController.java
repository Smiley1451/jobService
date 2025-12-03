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
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<String> createJob(@RequestBody @Valid JobRequest request) {
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


    @PatchMapping("/{jobId}/status")
    public Mono<Void> updateStatus(@PathVariable UUID jobId, @RequestParam String status) {
        return jobService.updateJobStatus(jobId, status);
    }
}