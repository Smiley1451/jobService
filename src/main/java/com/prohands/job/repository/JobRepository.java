package com.prohands.job.repository;

import com.prohands.job.entity.Job;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface JobRepository extends ReactiveCrudRepository<Job, UUID> {
    Flux<Job> findByProviderId(String providerId);
    @Query("""
        SELECT * FROM job 
        WHERE status = 'OPEN' 
        ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(latitude)) * cos(radians(longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(latitude)))) ASC 
        LIMIT :limit OFFSET :offset
    """)
    Flux<Job> findNearbyJobs(double lat, double lon, int limit, long offset);
}