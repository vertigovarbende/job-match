package com.deveyk.jobmatch.job.infrastructure.elasticsearch;

import com.deveyk.jobmatch.job.domain.model.JobStatusType;
import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Document(indexName = "job-search")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class JobDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long companyId;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private Seniority seniority;

    @Field(type = FieldType.Keyword)
    private EmploymentType employmentType;

    @Field(type = FieldType.Keyword)
    private WorkplaceType workplaceType;

    @Field(type = FieldType.Keyword)
    private String locationCountry;

    @Field(type = FieldType.Keyword)
    private String locationCity;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal salaryMinAmount;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal salaryMaxAmount;

    @Field(type = FieldType.Keyword)
    private String salaryCurrency;

    @Field(type = FieldType.Integer)
    private Integer minimumExperience;

    @Field(type = FieldType.Keyword)
    private JobStatusType status;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime publishedAt;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime expiresAt;

    @Field(type = FieldType.Long)
    private Set<Long> requiredSkillIds;

    @Field(type = FieldType.Long)
    private Set<Long> preferredSkillIds;

}
