package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.shootoutinfo.ShootoutSide;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShootoutInfo {

    private ShootoutSide home;
    private ShootoutSide away;

    public ShootoutSide getHome() {
        return home;
    }

    public void setHome(final ShootoutSide home) {
        this.home = home;
    }

    public ShootoutSide getAway() {
        return away;
    }

    public void setAway(final ShootoutSide away) {
        this.away = away;
    }

    @Override
    public String toString() {
        return "ShootoutInfo{" +
                "home=" + home +
                ", away=" + away +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShootoutInfo that = (ShootoutInfo) o;
        return Objects.equals(home, that.home) &&
                Objects.equals(away, that.away);
    }

    @Override
    public int hashCode() {
        return Objects.hash(home, away);
    }
}
