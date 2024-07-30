package com.aspira.jspider.service;

import com.aspira.jspider.model.Match;

import java.util.List;

public interface ReportGenerationService {

    void generateReport(List<Match> matches);
}