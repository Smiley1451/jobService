package com.prohands.job.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("job")
public class Job {

    @Id
    private UUID id;

    @Column("provider_id")
    private UUID providerId;

    private String title;
    private String description;
    private BigDecimal wage;

    private Double latitude;
    private Double longitude;


    @Column("required_skills")
    private String[] requiredSkills;

    private String status;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("source")
    private String source;
    @Column("number_of_employees")
    private Integer numberOfEmployees;

}