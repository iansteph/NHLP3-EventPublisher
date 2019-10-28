package iansteph.nhlp3.eventpublisher.model.nhl.gamedata;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameData {

    private Game game;
    private Datetime datetime;
    private Status status;
    private Teams teams;
    private Map<String, Player> players;
    private Venue venue;

    public Game getGame() {
        return game;
    }

    public void setGame(final Game game) {
        this.game = game;
    }

    public Datetime getDatetime() {
        return datetime;
    }

    public void setDateTime(final Datetime datetime) {
        this.datetime = datetime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Teams getTeams() {
        return teams;
    }

    public void setTeams(final Teams teams) {
        this.teams = teams;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(final Map<String, Player> players) {
        this.players = players;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(final Venue venue) {
        this.venue = venue;
    }

    @Override
    public String toString() {
        return "GameData{" +
                "game=" + game +
                ", datetime=" + datetime +
                ", status=" + status +
                ", teams=" + teams +
                ", players=" + players +
                ", venue=" + venue +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameData gameData = (GameData) o;
        return Objects.equals(game, gameData.game) &&
                Objects.equals(datetime, gameData.datetime) &&
                Objects.equals(status, gameData.status) &&
                Objects.equals(teams, gameData.teams) &&
                Objects.equals(players, gameData.players) &&
                Objects.equals(venue, gameData.venue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, datetime, status, teams, players, venue);
    }
}
