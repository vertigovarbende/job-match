package com.deveyk.jobmatch.shared.infrastructure.persistence.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityContextAuditorAware implements AuditorAware<String> {

    private static final String ANONYMOUS_PRINCIPAL = "anonymous";
    private static final String SYSTEM_AUDITOR = "JM";

    @Override
    public Optional<String> getCurrentAuditor() {

        final String auditor = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(user -> !ANONYMOUS_PRINCIPAL.equals(user))
                .map(Jwt.class::cast)
                .map(Jwt::getSubject)
                .orElse(SYSTEM_AUDITOR);

        return Optional.of(auditor);

    }

}
