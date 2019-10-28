package iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.*;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Play {

    private Result result;
    private About about;
    private Coordinates coordinates;
    private List<PlayPlayer> playPlayers;
    private Team team;

    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }

    public About getAbout() {
        return about;
    }

    public void setAbout(final About about) {
        this.about = about;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(final Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public List<PlayPlayer> getPlayers() {
        return playPlayers;
    }

    public void setPlayers(final List<PlayPlayer> playPlayers) {
        this.playPlayers = playPlayers;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(final Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Play{" +
                "result=" + result +
                ", about=" + about +
                ", coordinates=" + coordinates +
                ", playPlayers=" + playPlayers +
                ", team=" + team +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Play play = (Play) o;
        return Objects.equals(result, play.result) &&
                Objects.equals(about, play.about) &&
                Objects.equals(coordinates, play.coordinates) &&
                Objects.equals(playPlayers, play.playPlayers) &&
                Objects.equals(team, play.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, about, coordinates, playPlayers, team);
    }
}