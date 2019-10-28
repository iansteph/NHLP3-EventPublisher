package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.teams.TeamSide;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Teams {

    private TeamSide home;
    private TeamSide away;

    public TeamSide getHome() {
        return home;
    }

    public void setHome(final TeamSide home) {
        this.home = home;
    }

    public TeamSide getAway() {
        return away;
    }

    public void setAway(final TeamSide away) {
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
