package com.aspira.jspider.service.impl;

import com.aspira.jspider.model.*;
import com.aspira.jspider.service.ReportGenerationService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Implementation of the {@link ReportGenerationService} interface for generating reports of match data.
 * <p>
 * This service writes match data to a CSV file, where each row contains details about sports, leagues,
 * matches, markets, and runners. The file path for the report is configured via a property.
 * </p>
 *
 * @see ReportGenerationService
 * @see Match
 * @see League
 * @see Sport
 * @see Market
 * @see Runner
 */
@Slf4j
@Service
public class ReportGenerationServiceImpl implements ReportGenerationService {

    private static final String SEPARATOR = ", ";

    @Value("${report.file-path}")
    private String filePath;

    /**
     * Generates a CSV report for the provided list of matches.
     * <p>
     * This method writes details for each match to a CSV file specified by the {@code filePath} property.
     * The report includes information about the sport, league, match, market, and runners.
     * Each entry is separated by a comma and space as defined by the {@code SEPARATOR} constant.
     * </p>
     *
     * @param matches the list of {@link Match} objects to be included in the report
     */
    @Override
    public void generateReport(List<Match> matches) {
        log.debug("Request to generate report for bookmaker leon");
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            for (Match match : matches) {
                League league = match.getLeague();
                Sport sport = league.getSport();
                writer.writeNext(new String[]{sport.getName() + SEPARATOR + league.getName()});
                writer.writeNext(new String[]{match.getName() + SEPARATOR + match.getKickoff().toString() + SEPARATOR + match.getId()});

                for (Market market : match.getMarkets()) {
                    writer.writeNext(new String[]{market.getName()});
                    for (Runner runner : market.getRunners()) {
                        writer.writeNext(new String[]{"\t" + runner.getName() + SEPARATOR + runner.getValue() + SEPARATOR + runner.getId()});
                    }
                }
                writer.writeNext(new String[]{""});
            }
        } catch (IOException e) {
            log.error("Error writing report file.", e);
        }
    }
}
