package com.gmail.com.app.side.project;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;

@RestController
public class FantasyApiController {

    private static final String STATS_DIR = "D:\\programowanie\\basketball_reference_web_scraper\\basketball_reference_web_scraper\\";
    private static final String PLAYERS_SEASON_TOTALS_YEAR_FILE_NAME_SUFFIX = "_players_season_totals.json";
    private static final String SEASON_SCHEDULE_YEAR_FILE_NAME_SUFFIX = "_season_schedule.json";
    private static final String TEAMS_FILE_NAME= "teams.json";

    @GetMapping(value = "/players/pictures", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPlayersPicture() {
        JSONArray nbaComPlayers = FantasyUtil.getNbaComPlayers();

        nbaComPlayers.forEach(e-> {
            JSONObject ele = (JSONObject) e;
            ele.put("link", FantasyUtil.getNbaComImgHref(ele));
        });

        return new ResponseEntity<>(nbaComPlayers, HttpStatus.OK);
    }

    @GetMapping(value = "/nba_com_players/", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getNbaComPlayers() {
        return new ResponseEntity<>(FantasyUtil.getNbaComPlayers(), HttpStatus.OK);
    }

    @GetMapping(value = "/players/seasons", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getPlayersSeasonTotals(@RequestParam(name="year") String year) {
        return getNbaStats(year, PLAYERS_SEASON_TOTALS_YEAR_FILE_NAME_SUFFIX);
    }

    @GetMapping(value = "/schedules", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSeasonSchedule(@RequestParam(name="year") String year) {
        return getNbaStats(year, SEASON_SCHEDULE_YEAR_FILE_NAME_SUFFIX);
    }

    @GetMapping(value = "/teams", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTeams() {
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(STATS_DIR + TEAMS_FILE_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    private ResponseEntity<Object> getNbaStats(@RequestParam(name = "year") String year, String playersSeasonTotalsYearFileNameSuffix) {
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
        Object obj = null;
        try {
            String filename = year + playersSeasonTotalsYearFileNameSuffix;
            obj = parser.parse(new FileReader(STATS_DIR + filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(obj, HttpStatus.OK);
    }
}