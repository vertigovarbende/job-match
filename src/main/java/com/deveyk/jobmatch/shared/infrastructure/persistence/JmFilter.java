package com.deveyk.jobmatch.shared.infrastructure.persistence;

import org.springframework.data.jpa.domain.Specification;

/**
 * Infrastructure-ONLY temel filter arayuzu. T, bir JPA Entity'ye baglanmak zorunda oldugundan
 * (Specification<T> Criteria API'nin T'yi Entity gibi ele almasi gerekir), bunu implement eden
 * somut siniflar (or. SkillEntityFilter implements JmFilter<SkillEntity>) yalnizca Infrastructure
 * katmaninda, entity'lerinin yaninda yasar. Presentation/Application bu arayuzu veya
 * implementasyonlarini HICBIR ZAMAN dogrudan referans vermez (ArchitectureTest'in
 * "Infrastructure mayNotBeAccessedByAnyLayer" kurali) -- onun yerine duz/framework-agnostik
 * parametreler (or. bir String isim) kullanir, adapter bunlari icinde JmFilter implementasyonuna
 * cevirip Specification'a donusturur.
 */
public interface JmFilter<T> {

    Specification<T> toSpecification();

}
