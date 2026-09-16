package com.deveyk.jobmatch.company.application.service;

import com.deveyk.jobmatch.company.application.port.in.CompanyMembershipUseCase;
import com.deveyk.jobmatch.company.application.port.in.command.AddCompanyMembershipCommand;
import com.deveyk.jobmatch.company.application.port.in.command.RemoveCompanyMembershipCommand;
import com.deveyk.jobmatch.company.application.port.out.CompanyMembershipRepository;
import com.deveyk.jobmatch.company.domain.event.CompanyMemberAddedEvent;
import com.deveyk.jobmatch.company.domain.event.CompanyMemberRemovedEvent;
import com.deveyk.jobmatch.company.domain.exception.CompanyMembershipAlreadyExistsException;
import com.deveyk.jobmatch.company.domain.exception.CompanyMembershipNotFoundException;
import com.deveyk.jobmatch.company.domain.exception.CompanyMembershipRoleRequiredException;
import com.deveyk.jobmatch.company.domain.model.CompanyMembership;
import com.deveyk.jobmatch.identity.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyMembershipService implements CompanyMembershipUseCase {

    private final CompanyMembershipRepository companyMembershipRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public CompanyMembership addMember(final AddCompanyMembershipCommand command) {

        log.debug("Adding company member: companyId={}, userId={}", command.companyId(), command.userId());

        if (command.actorRole() != Role.EMPLOYER) {
            throw new CompanyMembershipRoleRequiredException(command.actorRole());
        }

        if (this.companyMembershipRepository.findByUserId(command.userId()).isPresent()) {
            throw new CompanyMembershipAlreadyExistsException(command.userId());
        }

        final CompanyMembership companyMembership = CompanyMembership.create(command.companyId(), command.userId());
        final CompanyMembership saved = this.companyMembershipRepository.save(companyMembership);

        this.applicationEventPublisher.publishEvent(new CompanyMemberAddedEvent(command.actorId(), String.valueOf(saved.getCompanyId()), saved.getUserId()));

        log.info("Company member added: companyId={}, userId={}", saved.getCompanyId(), saved.getUserId());

        return saved;
    }

    @Override
    @Transactional
    public void removeMember(final RemoveCompanyMembershipCommand command) {

        log.debug("Removing company member: companyId={}, userId={}", command.companyId(), command.userId());

        this.companyMembershipRepository.findByCompanyIdAndUserId(command.companyId(), command.userId())
                .orElseThrow(() -> new CompanyMembershipNotFoundException(command.companyId(), command.userId()));

        this.companyMembershipRepository.deleteByCompanyIdAndUserId(command.companyId(), command.userId());

        this.applicationEventPublisher.publishEvent(new CompanyMemberRemovedEvent(command.actorId(), String.valueOf(command.companyId()), command.userId()));

        log.info("Company member removed: companyId={}, userId={}", command.companyId(), command.userId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyMembership> listMembers(final Long companyId) {

        log.debug("Listing company members: companyId={}", companyId);

        return this.companyMembershipRepository.findAllByCompanyId(companyId);
    }

}
