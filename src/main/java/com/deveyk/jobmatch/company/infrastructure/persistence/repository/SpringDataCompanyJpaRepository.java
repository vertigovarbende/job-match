package com.deveyk.jobmatch.company.infrastructure.persistence.repository;

import com.deveyk.jobmatch.company.infrastructure.persistence.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCompanyJpaRepository extends JpaRepository<CompanyEntity, Long> {

}
