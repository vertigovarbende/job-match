package com.deveyk.jobmatch.shared.unit.domain.model;

import com.deveyk.jobmatch.shared.testsupport.SampleDomains.OtherSampleDomain;
import com.deveyk.jobmatch.shared.testsupport.SampleDomains.SampleDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JmBaseDomain - Birim Testleri")
class JmBaseDomainTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    @DisplayName("equals() id ayniysa diger alanlar ve timestamp'ler farkli olsa bile true doner")
    void equals_returnsTrue_whenIdsMatchDespiteDifferentFields() {

        final SampleDomain first = SampleDomain.builder()
                .id(1L)
                .name("A")
                .createdAt(NOW)
                .build();

        final SampleDomain second = SampleDomain.builder()
                .id(1L)
                .name("B")
                .createdAt(NOW.plusDays(1))
                .build();

        assertThat(first).isEqualTo(second);

    }

    @Test
    @DisplayName("equals() id farkliysa diger tum alanlar ayni olsa bile false doner")
    void equals_returnsFalse_whenIdsDiffer() {

        final SampleDomain first = SampleDomain.builder()
                .id(1L)
                .name("A")
                .createdAt(NOW)
                .build();

        final SampleDomain second = SampleDomain.builder()
                .id(2L)
                .name("A")
                .createdAt(NOW)
                .build();

        assertThat(first).isNotEqualTo(second);

    }

    @Test
    @DisplayName("equals() id ayni ama sinif farkliysa false doner")
    void equals_returnsFalse_whenSameIdButDifferentClass() {

        final SampleDomain first = SampleDomain.builder()
                .id(1L)
                .name("A")
                .createdAt(NOW)
                .build();

        final OtherSampleDomain second = OtherSampleDomain.builder()
                .id(1L)
                .createdAt(NOW)
                .build();

        assertThat(first).isNotEqualTo(second);

    }

    @Test
    @DisplayName("equals() iki nesnenin de id'si null ise (kaydedilmemis) farkli instance'lar icin false doner")
    void equals_returnsFalse_whenBothIdsAreNull() {

        final SampleDomain first = SampleDomain.builder()
                .id(null)
                .name("A")
                .createdAt(NOW)
                .build();

        final SampleDomain second = SampleDomain.builder()
                .id(null)
                .name("A")
                .createdAt(NOW)
                .build();

        assertThat(first).isNotEqualTo(second);

    }

    @Test
    @DisplayName("equals() id null olsa bile nesne kendisiyle esittir")
    void equals_returnsTrue_whenComparedWithItself_evenIfIdIsNull() {

        final SampleDomain entity = SampleDomain.builder()
                .id(null)
                .name("A")
                .createdAt(NOW)
                .build();

        assertThat(entity).isEqualTo(entity);

    }

    @Test
    @DisplayName("hashCode() esit nesneler icin ayni degeri doner")
    void hashCode_isSame_whenIdsMatchDespiteDifferentFields() {

        final SampleDomain first = SampleDomain.builder()
                .id(1L)
                .name("A")
                .createdAt(NOW)
                .build();

        final SampleDomain second = SampleDomain.builder()
                .id(1L)
                .name("B")
                .createdAt(NOW.plusDays(1))
                .build();

        assertThat(first).hasSameHashCodeAs(second);

    }

}
