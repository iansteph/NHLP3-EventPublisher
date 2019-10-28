package iansteph.nhlp3.eventpublisher.model.nhl.livedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveData {

    private Plays plays;
    private Linescore linescore;
    private Boxscore boxscore;
    private Decisions decisions;
    private ShotPressure shotPressure;

    public Plays getPlays() {
        return plays;
    }

    public void setPlays(final Plays plays) {
        this.plays = plays;
    }

    public Linescore getLinescore() {
        return linescore;
    }

    public void setLinescore(final Linescore linescore) {
        this.linescore = linescore;
    }

    public Boxscore getBoxscore() {
        return boxscore;
    }

    public void setBoxscore(final Boxscore boxscore) {
        this.boxscore = boxscore;
    }

    public Decisions getDecisions() {
        return decisions;
    }

    public void setDecisions(final Decisions decisions) {
        this.decisions = decisions;
    }

    public ShotPressure getShotPressure() {
        return shotPressure;
    }

    public void setShotPressure(final ShotPressure shotPressure) {
        this.shotPressure = shotPressure;
    }

    @Override
    public String toString() {
        return "LiveData{" +
                "plays=" + plays +
                ", linescore=" + linescore +
                ", boxscore=" + boxscore +
                ", decisions=" + decisions +
                ", shotPressure=" + shotPressure +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveData liveData = (LiveData) o;
        return Objects.equals(plays, liveData.plays) &&
                Objects.equals(linescore, liveData.linescore) &&
                Objects.equals(boxscore, liveData.boxscore) &&
                Objects.equals(decisions, liveData.decisions) &&
                Objects.equals(shotPressure, liveData.shotPressure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plays, linescore, boxscore, decisions, shotPressure);
    }
}
