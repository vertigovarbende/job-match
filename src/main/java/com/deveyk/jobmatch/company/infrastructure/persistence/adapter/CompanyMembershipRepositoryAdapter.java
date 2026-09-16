package com.deveyk.jobmatch.company.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.company.application.port.out.CompanyMembershipRepository;
import com.deveyk.jobmatch.company.domain.exception.CompanyFieldInvalidException;
import com.deveyk.jobmatch.company.domain.exception.CompanyMembershipAlreadyExistsException;
import com.deveyk.jobmatch.company.domain.model.CompanyMembership;
import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyMembershipEntity;
import com.deveyk.jobmatch.company.infrastructure.persistence.mapper.CompanyMembershipPersistenceMapper;
import com.deveyk.jobmatch.company.infrastructure.persistence.repository.SpringDataCompanyMembershipJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyMembershipRepositoryAdapter implements CompanyMembershipRepository {

    private static final String UNIQUE_CONSTRAINT_NAME = "uq_jm_company_membership_user";
    private static final String COMPANY_FK_CONSTRAINT_NAME = "fk_jm_company_membership_company";
    private static final String USER_FK_CONSTRAINT_NAME = "fk_jm_company_membership_user";

    private final SpringDataCompanyMembershipJpaRepository springDataCompanyMembershipJpaRepository;
    private final CompanyMembershipPersistenceMapper companyMembershipPersistenceMapper;

    @Override
    public Optional<CompanyMembership> findByCompanyIdAndUserId(final Long companyId, final Long userId) {
        return this.springDataCompanyMembershipJpaRepository.findByCompanyIdAndUserId(companyId, userId)
                .map(this.companyMembershipPersistenceMapper::toDomain);
    }

    @Override
    public List<CompanyMembership> findAllByCompanyId(final Long companyId) {
        return this.springDataCompanyMembershipJpaRepository.findAllByCompanyId(companyId).stream()
                .map(this.companyMembershipPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<CompanyMembership> findByUserId(final Long userId) {
        return this.springDataCompanyMembershipJpaRepository.findByUserId(userId)
                .map(this.companyMembershipPersistenceMapper::toDomain);
    }

    @Override
    public CompanyMembership save(final CompanyMembership companyMembership) {

        final CompanyMembershipEntity entity = this.companyMembershipPersistenceMapper.toEntity(companyMembership);

        try {

            final CompanyMembershipEntity saved = this.springDataCompanyMembershipJpaRepository.save(entity);
            return this.companyMembershipPersistenceMapper.toDomain(saved);

        } catch (DataIntegrityViolationException violation) {

            final String constraintName = extractConstraintName(violation);

            if (UNIQUE_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Duplicate jm_company_membership insert for userId={}", companyMembership.getUserId());
                throw new CompanyMembershipAlreadyExistsException(companyMembership.getUserId(), violation);

            }

            if (COMPANY_FK_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Invalid companyId={} referenced for userId={}", companyMembership.getCompanyId(), companyMembership.getUserId());
                throw new CompanyFieldInvalidException("companyId", "must reference an existing company");

            }

            if (USER_FK_CONSTRAINT_NAME.equals(constraintName)) {

                log.warn("Invalid userId={} referenced for companyId={}", companyMembership.getUserId(), companyMembership.getCompanyId());
                throw new CompanyFieldInvalidException("userId", "must reference an existing user");

            }

            throw violation;

        }
    }

    @Override
    public void deleteByCompanyIdAndUserId(final Long companyId, final Long userId) {
        this.springDataCompanyMembershipJpaRepository.deleteByCompanyIdAndUserId(companyId, userId);
    }

    private static String extractConstraintName(final DataIntegrityViolationException violation) {
        Throwable cause = violation.getCause();

        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation) {
                return constraintViolation.getConstraintName();
            }
            cause = cause.getCause();
        }

        return null;
    }

}
