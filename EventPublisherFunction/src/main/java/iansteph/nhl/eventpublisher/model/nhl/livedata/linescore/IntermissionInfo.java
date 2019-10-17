package iansteph.nhl.eventpublisher.model.nhl.livedata.linescore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntermissionInfo {

    private int intermissionTimeRemaining;
    private int intermissionTimeElapsed;
    private boolean inSituation;

    public int getIntermissionTimeRemaining() {
        return intermissionTimeRemaining;
    }

    public void setIntermissionTimeRemaining(final int intermissionTimeRemaining) {
        this.intermissionTimeRemaining = intermissionTimeRemaining;
    }

    public int getIntermissionTimeElapsed() {
        return intermissionTimeElapsed;
    }

    public void setIntermissionTimeElapsed(final int intermissionTimeElapsed) {
        this.intermissionTimeElapsed = intermissionTimeElapsed;
    }

    public boolean isInSituation() {
        return inSituation;
    }

    public void setInSituation(final boolean inSituation) {
        this.inSituation = inSituation;
    }

    @Override
    public String toString() {
        return "IntermissionInfo{" +
                "intermissionTimeRemaining=" + intermissionTimeRemaining +
                ", intermissionTimeElapsed=" + intermissionTimeElapsed +
                ", inSituation=" + inSituation +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntermissionInfo that = (IntermissionInfo) o;
        return intermissionTimeRemaining == that.intermissionTimeRemaining &&
                intermissionTimeElapsed == that.intermissionTimeElapsed &&
                inSituation == that.inSituation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(intermissionTimeRemaining, intermissionTimeElapsed, inSituation);
    }
}
