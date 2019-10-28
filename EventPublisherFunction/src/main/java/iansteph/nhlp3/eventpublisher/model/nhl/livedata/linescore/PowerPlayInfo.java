package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PowerPlayInfo {

    private int situationTimeRemaining;
    private int situationTimeElapsed;
    private boolean inSituation;

    public int getSituationTimeRemaining() {
        return situationTimeRemaining;
    }

    public void setSituationTimeRemaining(final int situationTimeRemaining) {
        this.situationTimeRemaining = situationTimeRemaining;
    }

    public int getSituationTimeElapsed() {
        return situationTimeElapsed;
    }

    public void setSituationTimeElapsed(final int situationTimeElapsed) {
        this.situationTimeElapsed = situationTimeElapsed;
    }

    public boolean isInSituation() {
        return inSituation;
    }

    public void setInSituation(final boolean inSituation) {
        this.inSituation = inSituation;
    }

    @Override
    public String toString() {
        return "PowerPlayInfo{" +
                "situationTimeRemaining=" + situationTimeRemaining +
                ", situationTimeElapsed=" + situationTimeElapsed +
                ", inSituation=" + inSituation +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PowerPlayInfo that = (PowerPlayInfo) o;
        return situationTimeRemaining == that.situationTimeRemaining &&
                situationTimeElapsed == that.situationTimeElapsed &&
                inSituation == that.inSituation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(situationTimeRemaining, situationTimeElapsed, inSituation);
    }
}
