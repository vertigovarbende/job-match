package com.deveyk.jobmatch.company.application.service;

import com.deveyk.jobmatch.company.application.port.in.CompanyProfileUseCase;
import com.deveyk.jobmatch.company.application.port.in.command.CreateCompanyCommand;
import com.deveyk.jobmatch.company.application.port.in.command.UpdateCompanyProfileCommand;
import com.deveyk.jobmatch.company.application.port.in.command.VerifyCompanyCommand;
import com.deveyk.jobmatch.company.application.port.out.CompanyRepository;
import com.deveyk.jobmatch.company.domain.event.CompanyVerifiedEvent;
import com.deveyk.jobmatch.company.domain.exception.CompanyNotFoundException;
import com.deveyk.jobmatch.company.domain.exception.CompanyRoleRequiredException;
import com.deveyk.jobmatch.company.domain.model.Company;
import com.deveyk.jobmatch.identity.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyProfileService implements CompanyProfileUseCase {

    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public Company createProfile(final CreateCompanyCommand command) {

        log.debug("Creating company profile: name={}, role={}", command.name(), command.role());

        if (command.role() != Role.EMPLOYER) {
            throw new CompanyRoleRequiredException(command.role());
        }

        final Company company = Company.create(command.name());
        final Company saved = this.companyRepository.save(company);

        log.info("Company profile created: companyId={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional
    public Company updateProfile(final UpdateCompanyProfileCommand command) {

        log.debug("Updating company profile: id={}", command.id());

        final Company company = this.findByIdOrThrow(command.id());

        company.updateProfile(
                command.name(),
                command.description(),
                command.industry(),
                command.website(),
                command.size(),
                command.headquarters()
        );

        final Company saved = this.companyRepository.save(company);

        log.info("Company profile updated: companyId={}", saved.getId());

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Company getProfile(final Long id) {

        log.debug("Fetching company profile: id={}", id);

        return this.findByIdOrThrow(id);

    }

    @Override
    @Transactional
    public Company verify(final VerifyCompanyCommand command) {

        log.debug("Verifying company: id={}", command.id());

        final Company company = this.findByIdOrThrow(command.id());
        final boolean transitioned = company.verify();
        final Company saved = this.companyRepository.save(company);

        if (transitioned) {

            this.applicationEventPublisher.publishEvent(new CompanyVerifiedEvent(command.actorId(), String.valueOf(saved.getId())));
            log.info("Company verified: companyId={}", saved.getId());

        } else {
            log.debug("Company already verified, no-op: companyId={}", saved.getId());
        }

        return saved;

    }

    private Company findByIdOrThrow(final Long id) {
        return this.companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
    }

}
