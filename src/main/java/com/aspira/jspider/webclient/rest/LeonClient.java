package com.aspira.jspider.webclient.rest;

import com.aspira.jspider.model.Betline;
import com.aspira.jspider.model.Match;

public interface LeonClient {

    Betline getMatchesByLeagueId(String id);

    Match getMatchDataById(String id);
}