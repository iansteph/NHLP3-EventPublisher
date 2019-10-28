package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside.teamstats.TeamSkaterStats;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamStats {

    private TeamSkaterStats teamSkaterStats;

    public TeamSkaterStats getTeamSkaterStats() {
        return teamSkaterStats;
    }

    public void setTeamSkaterStats(final TeamSkaterStats teamSkaterStats) {
        this.teamSkaterStats = teamSkaterStats;
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "teamSkaterStats=" + teamSkaterStats +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamStats teamStats = (TeamStats) o;
        return Objects.equals(teamSkaterStats, teamStats.teamSkaterStats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamSkaterStats);
    }
}
