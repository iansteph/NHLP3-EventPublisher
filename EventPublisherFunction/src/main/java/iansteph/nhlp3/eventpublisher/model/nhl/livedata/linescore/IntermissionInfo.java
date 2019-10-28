package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IntermissionInfo {

    private int intermissionTimeRemaining;
    private int intermissionTimeElapsed;
    private boolean inIntermission;

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

    public boolean isInIntermission() {
        return inIntermission;
    }

    public void setInIntermission(final boolean inIntermission) {
        this.inIntermission = inIntermission;
    }

    @Override
    public String toString() {
        return "IntermissionInfo{" +
                "intermissionTimeRemaining=" + intermissionTimeRemaining +
                ", intermissionTimeElapsed=" + intermissionTimeElapsed +
                ", inIntermission=" + inIntermission +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntermissionInfo that = (IntermissionInfo) o;
        return intermissionTimeRemaining == that.intermissionTimeRemaining &&
                intermissionTimeElapsed == that.intermissionTimeElapsed &&
                inIntermission == that.inIntermission;
    }

    @Override
    public int hashCode() {
        return Objects.hash(intermissionTimeRemaining, intermissionTimeElapsed, inIntermission);
    }
}
