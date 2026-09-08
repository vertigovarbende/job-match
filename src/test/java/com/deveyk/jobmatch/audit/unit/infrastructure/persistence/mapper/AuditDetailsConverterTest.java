package com.deveyk.jobmatch.audit.unit.infrastructure.persistence.mapper;

import com.deveyk.jobmatch.audit.infrastructure.persistence.mapper.AuditDetailsConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("AuditDetailsConverter - Birim Testleri")
class AuditDetailsConverterTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final AuditDetailsConverter converter = new AuditDetailsConverter(this.objectMapper);

    @Test
    @DisplayName("toMap() details null olduğunda null döner")
    void toMap_returnsNull_whenDetailsIsNull() {
        assertThat(this.converter.toMap(null)).isNull();
    }

    @Test
    @DisplayName("toMap() details zaten bir Map olduğunda aynı map'i döner")
    void toMap_returnsSameMap_whenDetailsIsAlreadyAMap() {
        final Map<String, Object> details = Map.of("oldStatus", "SUBMITTED", "newStatus", "UNDER_REVIEW");

        final Map<String, Object> result = this.converter.toMap(details);

        assertThat(result).isEqualTo(details);
    }

    @Test
    @DisplayName("toMap() bir POJO/record'u Map'e dönüştürür")
    void toMap_convertsPojoToMap() {
        final SamplePayload payload = new SamplePayload("policy-violation", 2);

        final Map<String, Object> result = this.converter.toMap(payload);

        assertThat(result)
                .containsEntry("reason", "policy-violation")
                .containsEntry("attempt", 2);
    }

    private record SamplePayload(String reason, int attempt) {
    }

}
