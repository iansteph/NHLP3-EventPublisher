package iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.about;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Goals {

    private int home;
    private int away;

    public int getHome() {
        return home;
    }

    public void setHome(final int home) {
        this.home = home;
    }

    public int getAway() {
        return away;
    }

    public void setAway(final int away) {
        this.away = away;
    }

    @Override
    public String toString() {
        return "Goals{" +
                "home=" + home +
                ", away=" + away +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goals goals = (Goals) o;
        return home == goals.home &&
                away == goals.away;
    }

    @Override
    public int hashCode() {
        return Objects.hash(home, away);
    }
}
