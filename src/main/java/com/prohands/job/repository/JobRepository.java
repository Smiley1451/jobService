package com.prohands.job.repository;

import com.prohands.job.entity.Job;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface JobRepository extends ReactiveCrudRepository<Job, UUID> {
    Flux<Job> findByProviderId(String providerId);
}