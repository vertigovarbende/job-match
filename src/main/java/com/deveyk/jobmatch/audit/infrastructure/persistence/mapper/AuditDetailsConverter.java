package com.deveyk.jobmatch.audit.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class AuditDetailsConverter {

    private final ObjectMapper objectMapper;

    public AuditDetailsConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> toMap(Object details) {
        if (details == null) {
            return null;
        }
        if (details instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> castedMap = (Map<String, Object>) map;
            return castedMap;
        }
        return objectMapper.convertValue(details, new TypeReference<Map<String, Object>>() {
        });
    }

}
