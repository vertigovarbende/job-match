package com.deveyk.jobmatch.shared.infrastructure.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "securityContextAuditorAware")
public class JpaAuditingConfig {
}
