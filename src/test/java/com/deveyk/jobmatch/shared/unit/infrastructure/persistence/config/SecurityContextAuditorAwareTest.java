package com.deveyk.jobmatch.shared.unit.infrastructure.persistence.config;

import com.deveyk.jobmatch.shared.infrastructure.persistence.config.SecurityContextAuditorAware;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SecurityContextAuditorAware - Birim Testleri")
class SecurityContextAuditorAwareTest {

    private final SecurityContextAuditorAware auditorAware = new SecurityContextAuditorAware();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getCurrentAuditor() authentication principal'i Jwt ise subject'i doner")
    void getCurrentAuditor_returnsJwtSubject_whenPrincipalIsJwt() {

        final Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user-123");

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final Optional<String> result = this.auditorAware.getCurrentAuditor();

        assertThat(result).contains("user-123");

    }

    @Test
    @DisplayName("getCurrentAuditor() authentication yoksa JM doner")
    void getCurrentAuditor_returnsSystemAuditor_whenNoAuthentication() {

        SecurityContextHolder.clearContext();

        final Optional<String> result = this.auditorAware.getCurrentAuditor();

        assertThat(result).contains("JM");

    }

    @Test
    @DisplayName("getCurrentAuditor() principal anonymous ise JM doner")
    void getCurrentAuditor_returnsSystemAuditor_whenPrincipalIsAnonymous() {

        final Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("anonymous");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final Optional<String> result = this.auditorAware.getCurrentAuditor();

        assertThat(result).contains("JM");

    }

}
