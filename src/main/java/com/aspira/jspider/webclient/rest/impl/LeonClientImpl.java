package com.aspira.jspider.webclient.rest.impl;

import com.aspira.jspider.model.Betline;
import com.aspira.jspider.model.Match;
import com.aspira.jspider.webclient.rest.LeonClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Implementation of the {@link LeonClient} interface for interacting with the Leon API.
 * <p>
 * This service uses a {@link WebClient} to make HTTP requests to the Leon API to retrieve data about matches
 * and events. It supports fetching matches by league ID and detailed match data by event ID.
 * </p>
 *
 * @see LeonClient
 * @see WebClient
 * @see Betline
 * @see Match
 */
@Service
@RequiredArgsConstructor
public class LeonClientImpl implements LeonClient {

    private static final String C_TAG = "ctag";
    private static final String EN_US = "en-US";
    private static final String FLAGS = "flags";
    private static final String EVENT_ID = "eventId";
    private static final String LEAGUE_ID = "league_id";
    private static final String HIDE_CLOSED = "hideClosed";
    private static final String ALL_EVENTS_FLAGS = "reg,urlv2,mm2,rrc,nodup";
    private static final String EVENT_FLAGS = "reg,urlv2,mm2,rrc,nodup,smg,outv2";

    @Value("${web-client.event-path}")
    private String eventPath;

    @Value("${web-client.all-events-path}")
    private String allEventsPath;

    private final WebClient leonWebClient;

    /**
     * Retrieves a {@link Betline} object containing matches for a specified league ID.
     * <p>
     * This method constructs a URL using the provided league ID and several query parameters to request
     * data from the Leon API. It then retrieves and returns the response as a {@link Betline} object.
     * </p>
     *
     * @param id the ID of the league to fetch matches for
     * @return a {@link Betline} object containing the matches for the specified league
     */
    @Override
    public Betline getMatchesByLeagueId(String id) {
        return leonWebClient.get()
                .uri(UriComponentsBuilder.fromUriString(allEventsPath)
                        .queryParam(C_TAG, EN_US)
                        .queryParam(LEAGUE_ID, id)
                        .queryParam(HIDE_CLOSED, true)
                        .queryParam(FLAGS, ALL_EVENTS_FLAGS)
                        .build()
                        .toUriString())
                .retrieve()
                .bodyToMono(Betline.class)
                .block();
    }

    /**
     * Retrieves a {@link Match} object containing details for a specified event ID.
     * <p>
     * This method constructs a URL using the provided event ID and several query parameters to request
     * data from the Leon API. It then retrieves and returns the response as a {@link Match} object.
     * </p>
     *
     * @param id the ID of the event to fetch match data for
     * @return a {@link Match} object containing details for the specified event
     */
    @Override
    public Match getMatchDataById(String id) {
        return leonWebClient.get()
                .uri(UriComponentsBuilder.fromUriString(eventPath)
                        .queryParam(C_TAG, EN_US)
                        .queryParam(EVENT_ID, id)
                        .queryParam(FLAGS, EVENT_FLAGS)
                        .build()
                        .toUriString())
                .retrieve()
                .bodyToMono(Match.class)
                .block();
    }
}