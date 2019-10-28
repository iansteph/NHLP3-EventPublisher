package iansteph.nhlp3.eventpublisher.model.nhl.gamedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams.Team;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Teams {

    private Team home;
    private Team away;

    public Team getHome() {
        return home;
    }

    public void setHome(final Team home) {
        this.home = home;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(final Team away) {
        this.away = away;
    }

    @Override
    public String toString() {
        return "Teams{" +
                "home=" + home +
                ", away=" + away +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teams teams = (Teams) o;
        return Objects.equals(home, teams.home) &&
                Objects.equals(away, teams.away);
    }

    @Override
    public int hashCode() {
        return Objects.hash(home, away);
    }
}
