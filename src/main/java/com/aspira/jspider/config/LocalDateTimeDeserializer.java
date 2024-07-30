package com.aspira.jspider.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Custom deserializer for {@link LocalDateTime} used in Jackson JSON processing.
 * This class extends {@link JsonDeserializer<LocalDateTime>} and is responsible for
 * deserializing JSON content into a {@link LocalDateTime} instance.
 * <p>
 * This deserializer assumes that the JSON content is a timestamp in milliseconds
 * since the epoch (1970-01-01T00:00:00Z) and converts it to a {@link LocalDateTime}
 * in the UTC time zone.
 * </p>
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        long timestamp = p.getLongValue();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
    }
}
