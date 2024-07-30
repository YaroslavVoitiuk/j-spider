package com.aspira.jspider.model;

import lombok.Data;

import java.util.List;

@Data
public class Market {

    private String id;
    private String name;
    private List<Runner> runners;
}
