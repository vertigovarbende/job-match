package com.deveyk.jobmatch.company.infrastructure.persistence.adapter;

import com.deveyk.jobmatch.company.application.port.out.CompanyRepository;
import com.deveyk.jobmatch.company.domain.model.Company;
import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import com.deveyk.jobmatch.company.infrastructure.persistence.mapper.CompanyPersistenceMapper;
import com.deveyk.jobmatch.company.infrastructure.persistence.repository.SpringDataCompanyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepository {

    private final SpringDataCompanyJpaRepository springDataCompanyJpaRepository;
    private final CompanyPersistenceMapper companyPersistenceMapper;

    @Override
    public Optional<Company> findById(final Long id) {
        return this.springDataCompanyJpaRepository.findById(id)
                .map(this.companyPersistenceMapper::toDomain);
    }

    @Override
    public Company save(final Company company) {
        final CompanyEntity entity = this.companyPersistenceMapper.toEntity(company);
        final CompanyEntity saved = this.springDataCompanyJpaRepository.save(entity);
        return this.companyPersistenceMapper.toDomain(saved);
    }

}
