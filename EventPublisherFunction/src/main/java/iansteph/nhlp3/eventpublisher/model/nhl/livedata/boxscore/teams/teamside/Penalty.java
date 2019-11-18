package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside;

import java.util.Objects;

public class Penalty {

    private int id;
    private String timeRemaining;
    private boolean active;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(final String timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Penalty{" +
                "id=" + id +
                ", timeRemaining='" + timeRemaining + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Penalty penalty = (Penalty) o;
        return id == penalty.id &&
                active == penalty.active &&
                Objects.equals(timeRemaining, penalty.timeRemaining);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeRemaining, active);
    }
}
