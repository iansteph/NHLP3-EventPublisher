package iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure.gamepressure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    private int pressure;

    public int getPressure() {
        return pressure;
    }

    public void setPressure(final int pressure) {
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "Team{" +
                "pressure=" + pressure +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return pressure == team.pressure;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pressure);
    }
}
