package com.aspira.jspider.service.impl;


import com.aspira.jspider.model.Event;
import com.aspira.jspider.model.Match;
import com.aspira.jspider.service.LeonBetsParser;
import com.aspira.jspider.service.ReportGenerationService;
import com.aspira.jspider.webclient.rest.LeonClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link LeonBetsParser} interface responsible for parsing bets data from
 * sports-related HTML pages and generating reports based on the parsed data.
 * <p>
 * This service uses a fixed thread pool to process multiple sports pages concurrently. It retrieves
 * match data from HTML files and makes HTTP requests to fetch detailed betting data. After processing,
 * it generates a report based on the collected data.
 * </p>
 *
 * @see LeonBetsParser
 * @see LeonClient
 * @see ReportGenerationService
 * @see Match
 * @see Event
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeonBetsParserImpl implements LeonBetsParser {

    private static final int MATCHES_TO_PROCESS = 2;
    private static final String ATTRIBUTE_KEY = "href";
    private static final String PAGE_PREFIX = "sport-pages/";
    private static final String REGEX = "(\\d+)-[a-zA-Z0-9-]+";
    private static final String CSS_QUERY = "a.sports-sidebar-top-leagues__league_Rd8VZ";
    private static final List<String> SPORT_PAGES = List.of("football.html", "tennis.html", "basketBall.html", "esports.html");

    private final LeonClient leonClient;
    private final ReportGenerationService reportGenerationService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    /**
     * Parses bets data for each sport page concurrently and generates a report based on the collected data.
     * <p>
     * This method processes each sport page in parallel using a fixed thread pool. It collects match data
     * from each page and then generates a report using the {@link ReportGenerationService}.
     * </p>
     */
    @Override
    public void parseBetsData() {
        log.info("Request to parse matches data for sports: {}", SPORT_PAGES);
        final var futures = SPORT_PAGES.stream()
                .map(sport -> CompletableFuture.supplyAsync(() -> parseBetsDataConcurrently(PAGE_PREFIX + sport), executorService))
                .toList();
        final var matches = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        executorService.shutdown();
        reportGenerationService.generateReport(matches);
    }


    /**
     * Parses bets data for a specific sport page.
     * <p>
     * This method reads an HTML file, extracts league IDs using a regular expression, and retrieves match
     * data for each league ID. It handles the file I/O and data extraction processes and returns a list of
     * matches.
     * </p>
     *
     * @param fileName the name of the HTML file containing the sport data
     * @return a list of {@link Match} objects containing betting data
     * @throws RuntimeException if an error occurs while reading the file or parsing data
     */
    private List<Match> parseBetsDataConcurrently(String fileName) {
        try {
            log.info("Request to parse matches data for sport: {}", fileName);

            Document document = Jsoup.parse(new File(fileName), Charset.defaultCharset().displayName());
            Elements topLeagues = document.select(CSS_QUERY);
            Pattern pattern = Pattern.compile(REGEX);

            List<String> topLeaguesIds = topLeagues.stream()
                    .map(tl -> {
                        Matcher matcher = pattern.matcher(tl.attr(ATTRIBUTE_KEY));
                        return matcher.find() ? matcher.group(1) : null;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            return topLeaguesIds.stream()
                    .map(this::processTopLeaguesMatches)
                    .flatMap(List::stream)
                    .toList();

        } catch (IOException e) {
            log.error("Error while parsing the file.", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Retrieves match data for a specific league ID.
     * <p>
     * This method fetches events associated with the league ID and retrieves detailed betting data for each
     * match. It limits the number of matches to process based on the {@code MATCHES_TO_PROCESS} constant.
     * </p>
     *
     * @param id the league ID to retrieve match data for
     * @return a list of {@link Match} objects for the specified league
     */
    private List<Match> processTopLeaguesMatches(String id) {
        log.info("Request to get all matches by league id {}", id);
        List<Event> events = leonClient.getMatchesByLeagueId(id).getEvents();
        return events.stream()
                .limit(MATCHES_TO_PROCESS)
                .map(event -> {
                    log.info("Request to get betting data for the match with id {}", event.getId());
                    return leonClient.getMatchDataById(event.getId());
                })
                .toList();
    }

}