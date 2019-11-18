package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamSide {

    private Team team;
    private TeamStats teamStats;
    private Map<String, Player> players;
    private List<Integer> goalies;
    private List<Integer> skaters;
    private List<Integer> onIce;
    private List<OnIcePlus> onIcePlus;
    private List<Integer> scratches;
    private List<Penalty> penaltyBox;
    private List<Coach> coaches;

    public Team getTeam() {
        return team;
    }

    public void setTeam(final Team team) {
        this.team = team;
    }

    public TeamStats getTeamStats() {
        return teamStats;
    }

    public void setTeamStats(final TeamStats teamStats) {
        this.teamStats = teamStats;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setPlayers(final Map<String, Player> players) {
        this.players = players;
    }

    public List<Integer> getGoalies() {
        return goalies;
    }

    public void setGoalies(final List<Integer> goalies) {
        this.goalies = goalies;
    }

    public List<Integer> getSkaters() {
        return skaters;
    }

    public void setSkaters(final List<Integer> skaters) {
        this.skaters = skaters;
    }

    public List<Integer> getOnIce() {
        return onIce;
    }

    public void setOnIce(final List<Integer> onIce) {
        this.onIce = onIce;
    }

    public List<OnIcePlus> getOnIcePlus() {
        return onIcePlus;
    }

    public void setOnIcePlus(final List<OnIcePlus> onIcePlus) {
        this.onIcePlus = onIcePlus;
    }

    public List<Integer> getScratches() {
        return scratches;
    }

    public void setScratches(final List<Integer> scratches) {
        this.scratches = scratches;
    }

    public List<Penalty> getPenaltyBox() {
        return penaltyBox;
    }

    public void setPenaltyBox(final List<Penalty> penaltyBox) {
        this.penaltyBox = penaltyBox;
    }

    public List<Coach> getCoaches() {
        return coaches;
    }

    public void setCoaches(final List<Coach> coaches) {
        this.coaches = coaches;
    }

    @Override
    public String toString() {
        return "TeamSide{" +
                "team=" + team +
                ", teamStats=" + teamStats +
                ", players=" + players +
                ", goalies=" + goalies +
                ", skaters=" + skaters +
                ", onIce=" + onIce +
                ", onIcePlus=" + onIcePlus +
                ", scratches=" + scratches +
                ", penaltyBox=" + penaltyBox +
                ", coaches=" + coaches +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamSide teamSide = (TeamSide) o;
        return Objects.equals(team, teamSide.team) &&
                Objects.equals(teamStats, teamSide.teamStats) &&
                Objects.equals(players, teamSide.players) &&
                Objects.equals(goalies, teamSide.goalies) &&
                Objects.equals(skaters, teamSide.skaters) &&
                Objects.equals(onIce, teamSide.onIce) &&
                Objects.equals(onIcePlus, teamSide.onIcePlus) &&
                Objects.equals(scratches, teamSide.scratches) &&
                Objects.equals(penaltyBox, teamSide.penaltyBox) &&
                Objects.equals(coaches, teamSide.coaches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, teamStats, players, goalies, skaters, onIce, onIcePlus, scratches, penaltyBox, coaches);
    }
}
