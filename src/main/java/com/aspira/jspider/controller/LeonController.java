package com.aspira.jspider.controller;


import com.aspira.jspider.service.LeonBetsParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling requests related to analyzing bookmaker data for "Leon".
 * <p>
 * This controller exposes an endpoint to trigger the parsing of bets data from the "Leon" bookmaker.
 * It uses the {@link LeonBetsParser} service to process the data and provides a simple confirmation response.
 * </p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *   <li><code>POST /api/analyze-leon</code> - Triggers the parsing of bets data from "Leon".</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * POST /api/analyze-leon
 * </pre>
 *
 * @see LeonBetsParser
 * @see ResponseEntity
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeonController {

    private final LeonBetsParser leonBetsParser;

    /**
     * Handles the HTTP POST request to the endpoint <code>/api/analyze-leon</code>.
     * <p>
     * Logs the request for debugging purposes and delegates the data parsing task to the {@link LeonBetsParser}.
     * </p>
     *
     * @return a {@link ResponseEntity} with a message indicating that the data was processed successfully
     */
    @PostMapping("/analyze-leon")
    public ResponseEntity<String> analyzeLeon() {
        log.debug("REST request to analyze bookmaker leon");
        leonBetsParser.parseBetsData();

        return ResponseEntity.ok("Data successfully processed");
    }

}