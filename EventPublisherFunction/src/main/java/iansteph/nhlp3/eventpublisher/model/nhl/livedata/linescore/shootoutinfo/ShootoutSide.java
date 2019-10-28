package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.shootoutinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShootoutSide {

    private int scores;
    private int attempts;

    public int getScores() {
        return scores;
    }

    public void setScores(final int scores) {
        this.scores = scores;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(final int attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "ShootoutSide{" +
                "scores=" + scores +
                ", attempts=" + attempts +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShootoutSide that = (ShootoutSide) o;
        return scores == that.scores &&
                attempts == that.attempts;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scores, attempts);
    }
}
