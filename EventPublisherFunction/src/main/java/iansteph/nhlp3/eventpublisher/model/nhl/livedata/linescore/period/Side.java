package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Side {

    private int goals;
    private int shotsOnGoal;
    private String rinkSide;

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

    public String getRinkSide() {
        return rinkSide;
    }

    public void setRinkSide(final String rinkSide) {
        this.rinkSide = rinkSide;
    }

    @Override
    public String toString() {
        return "Side{" +
                "goals=" + goals +
                ", shotsOnGoal=" + shotsOnGoal +
                ", rinkSide='" + rinkSide + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Side side = (Side) o;
        return goals == side.goals &&
                shotsOnGoal == side.shotsOnGoal &&
                Objects.equals(rinkSide, side.rinkSide);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goals, shotsOnGoal, rinkSide);
    }
}
