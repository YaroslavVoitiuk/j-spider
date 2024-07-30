package com.aspira.jspider.model;

import com.aspira.jspider.config.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
public class Match {

    private String id;
    private String name;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime kickoff;
    private League league;
    private List<Market> markets;

}