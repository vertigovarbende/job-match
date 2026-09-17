package com.deveyk.jobmatch.catalog.infrastructure.persistence.filter;

import com.deveyk.jobmatch.catalog.infrastructure.persistence.entity.SkillEntity;
import com.deveyk.jobmatch.shared.infrastructure.persistence.JmFilter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;


@Getter
@Builder
public class SkillEntityFilter implements JmFilter<SkillEntity> {

    private final String name;

    @Override
    public Specification<SkillEntity> toSpecification() {

        Specification<SkillEntity> specification = Specification.unrestricted();

        if (this.name != null && !this.name.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + this.name.toUpperCase() + "%"));
        }

        return specification;
    }

}
