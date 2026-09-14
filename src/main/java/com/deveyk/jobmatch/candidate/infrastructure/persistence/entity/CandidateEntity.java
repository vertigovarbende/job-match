package com.deveyk.jobmatch.candidate.infrastructure.persistence.entity;

import com.deveyk.jobmatch.shared.infrastructure.persistence.entity.JmBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "jm_candidate")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CandidateEntity extends JmBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "headline")
    private String headline;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "location_country")
    private String locationCountry;

    @Column(name = "location_city")
    private String locationCity;

    @Column(name = "desired_salary_min_amount", precision = 19, scale = 2)
    private BigDecimal desiredSalaryMinAmount;

    @Column(name = "desired_salary_max_amount", precision = 19, scale = 2)
    private BigDecimal desiredSalaryMaxAmount;

    @Column(name = "desired_salary_currency", length = 3)
    private String desiredSalaryCurrency;

    @Column(name = "open_to_on_site")
    private Boolean openToOnSite;

    @Column(name = "open_to_remote")
    private Boolean openToRemote;

    @Column(name = "open_to_hybrid")
    private Boolean openToHybrid;

}