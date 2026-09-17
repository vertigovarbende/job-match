package com.deveyk.jobmatch.job.domain.exception;

import com.deveyk.jobmatch.job.domain.JobErrorCode;
import com.deveyk.jobmatch.shared.domain.exception.DomainRuleViolationException;

import java.io.Serial;
import java.util.Set;

public class DuplicateSkillReferenceException extends DomainRuleViolationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DuplicateSkillReferenceException(final Set<Long> skillIds) {
        super(JobErrorCode.DUPLICATE_SKILL_REFERENCE, "Skill id(s) " + skillIds + " cannot be both required and preferred");
    }

}
