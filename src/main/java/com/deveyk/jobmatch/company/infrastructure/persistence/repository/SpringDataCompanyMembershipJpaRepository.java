package com.deveyk.jobmatch.company.infrastructure.persistence.repository;

import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataCompanyMembershipJpaRepository extends JpaRepository<CompanyMembershipEntity, Long> {

    Optional<CompanyMembershipEntity> findByCompanyIdAndUserId(Long companyId, Long userId);

    List<CompanyMembershipEntity> findAllByCompanyId(Long companyId);

    Optional<CompanyMembershipEntity> findByUserId(Long userId);

    void deleteByCompanyIdAndUserId(Long companyId, Long userId);

}
