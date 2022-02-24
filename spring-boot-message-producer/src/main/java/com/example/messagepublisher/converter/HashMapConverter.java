package com.example.messagepublisher.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(HashMapConverter.class);

    @Override
    public String convertToDatabaseColumn(Map<String, Object> messageAttributes) {
        String messageAttributesJson = null;
        try {

            messageAttributesJson = objectMapper.writeValueAsString(messageAttributes);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return messageAttributesJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String messageAttributesJson) {
        Map<String, Object> messageAttributes = null;
        try {
            messageAttributes = objectMapper.readValue(messageAttributesJson, Map.class);
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }

        return messageAttributes;
    }
}
