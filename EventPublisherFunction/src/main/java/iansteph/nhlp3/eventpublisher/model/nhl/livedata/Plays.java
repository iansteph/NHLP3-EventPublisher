package iansteph.nhlp3.eventpublisher.model.nhl.livedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Period;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Plays {

    private List<Play> allPlays;
    private List<Integer> scoringPlays;
    private List<Integer> penaltyPlays;
    private List<Period> playsByPeriod;
    private Play currentPlay;

    public List<Play> getAllPlays() {
        return allPlays;
    }

    public void setAllPlays(final List<Play> allPlays) {
        this.allPlays = allPlays;
    }

    public List<Integer> getScoringPlays() {
        return scoringPlays;
    }

    public void setScoringPlays(final List<Integer> scoringPlays) {
        this.scoringPlays = scoringPlays;
    }

    public List<Integer> getPenaltyPlays() {
        return penaltyPlays;
    }

    public void setPenaltyPlays(final List<Integer> penaltyPlays) {
        this.penaltyPlays = penaltyPlays;
    }

    public List<Period> getPlaysByPeriod() {
        return playsByPeriod;
    }

    public void setPlaysByPeriod(final List<Period> playsByPeriod) {
        this.playsByPeriod = playsByPeriod;
    }

    public Play getCurrentPlay() {
        return currentPlay;
    }

    public void setCurrentPlay(final Play currentPlay) {
        this.currentPlay = currentPlay;
    }

    @Override
    public String toString() {
        return "Plays{" +
                "allPlays=" + allPlays +
                ", scoringPlays=" + scoringPlays +
                ", penaltyPlays=" + penaltyPlays +
                ", playsByPeriod=" + playsByPeriod +
                ", currentPlay=" + currentPlay +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plays plays = (Plays) o;
        return Objects.equals(allPlays, plays.allPlays) &&
                Objects.equals(scoringPlays, plays.scoringPlays) &&
                Objects.equals(penaltyPlays, plays.penaltyPlays) &&
                Objects.equals(playsByPeriod, plays.playsByPeriod) &&
                Objects.equals(currentPlay, plays.currentPlay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allPlays, scoringPlays, penaltyPlays, playsByPeriod, currentPlay);
    }
}
