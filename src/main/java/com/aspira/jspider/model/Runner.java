package com.aspira.jspider.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class Runner {

    private String id;
    @JsonAlias("priceStr")
    private String value;
    private String name;
}