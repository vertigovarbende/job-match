package com.deveyk.jobmatch.job.presentation.rest.request;

import com.deveyk.jobmatch.shared.domain.EmploymentType;
import com.deveyk.jobmatch.shared.domain.Seniority;
import com.deveyk.jobmatch.shared.domain.WorkplaceType;
import com.deveyk.jobmatch.shared.presentation.rest.request.JmPagingRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSearchRequest extends JmPagingRequest {

    private static final Set<String> ACCEPTED_ORDER_PROPERTIES = Set.of("id", "title", "publishedAt");

    @Size(min = 1, max = 200)
    private String q;

    private Seniority seniority;

    private EmploymentType employmentType;

    private WorkplaceType workplaceType;

    private String locationCountry;

    private String locationCity;

    @DecimalMin("0")
    private BigDecimal salaryMin;

    private Set<Long> skillIds;

    @JsonIgnore
    @AssertTrue
    @Override
    public boolean isOrderPropertyAccepted() {
        return this.isPropertyAccepted(ACCEPTED_ORDER_PROPERTIES);
    }

}
