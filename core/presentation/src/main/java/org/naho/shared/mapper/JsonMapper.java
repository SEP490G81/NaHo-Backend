package org.naho.shared.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JsonMapper {

    private final ObjectMapper objectMapper;

    @Named("convertObjectToJsonString")
    public String convertObjectToJsonString(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Named("stringToObject")
    public Object stringToObject(String str) {
        if (str == null || str.isBlank()) return null;
        try {
            return objectMapper.readTree(str);
        } catch (JsonProcessingException e) {
            return str;
        }
    }
}
