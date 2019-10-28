package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.teams.teamside.TeamSideTeam;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamSide {

    private TeamSideTeam team;
    private int goals;
    private int shotsOnGoal;
    private boolean goaliePulled;
    private int numSkaters;
    private boolean powerPlay;

    public TeamSideTeam getTeam() {
        return team;
    }

    public void setTeam(final TeamSideTeam team) {
        this.team = team;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(final int goals) {
        this.goals = goals;
    }

    public int getShotsOnGoal() {
        return shotsOnGoal;
    }

    public void setShotsOnGoal(final int shotsOnGoal) {
        this.shotsOnGoal = shotsOnGoal;
    }

    public boolean isGoaliePulled() {
        return goaliePulled;
    }

    public void setGoaliePulled(final boolean goaliePulled) {
        this.goaliePulled = goaliePulled;
    }

    public int getNumSkaters() {
        return numSkaters;
    }

    public void setNumSkaters(final int numSkaters) {
        this.numSkaters = numSkaters;
    }

    public boolean isPowerPlay() {
        return powerPlay;
    }

    public void setPowerPlay(final boolean powerPlay) {
        this.powerPlay = powerPlay;
    }

    @Override
    public String toString() {
        return "TeamSide{" +
                "team=" + team +
                ", goals=" + goals +
                ", shotsOnGoal=" + shotsOnGoal +
                ", goaliePulled=" + goaliePulled +
                ", numSkaters=" + numSkaters +
                ", powerPlay=" + powerPlay +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamSide teamSide = (TeamSide) o;
        return goals == teamSide.goals &&
                shotsOnGoal == teamSide.shotsOnGoal &&
                goaliePulled == teamSide.goaliePulled &&
                numSkaters == teamSide.numSkaters &&
                powerPlay == teamSide.powerPlay &&
                Objects.equals(team, teamSide.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, goals, shotsOnGoal, goaliePulled, numSkaters, powerPlay);
    }
}
