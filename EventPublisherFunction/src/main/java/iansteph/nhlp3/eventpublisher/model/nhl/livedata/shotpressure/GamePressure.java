package iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure.gamepressure.Team;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GamePressure {

    private int timeOffset;
    private int eventIdx;
    private Team homeTeam;
    private Team awayTeam;

    public int getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(final int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public int getEventIdx() {
        return eventIdx;
    }

    public void setEventIdx(final int eventIdx) {
        this.eventIdx = eventIdx;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(final Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(final Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    @Override
    public String toString() {
        return "GamePressure{" +
                "timeOffset=" + timeOffset +
                ", eventIdx=" + eventIdx +
                ", homeTeam=" + homeTeam +
                ", awayTeam=" + awayTeam +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePressure that = (GamePressure) o;
        return timeOffset == that.timeOffset &&
                eventIdx == that.eventIdx &&
                Objects.equals(homeTeam, that.homeTeam) &&
                Objects.equals(awayTeam, that.awayTeam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeOffset, eventIdx, homeTeam, awayTeam);
    }
}
