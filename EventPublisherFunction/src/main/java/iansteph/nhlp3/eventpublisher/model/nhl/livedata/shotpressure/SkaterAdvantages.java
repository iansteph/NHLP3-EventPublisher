package iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure.skateradvantages.SkaterAdvantage;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SkaterAdvantages {

    private List<SkaterAdvantage> homeTeam;
    private List<SkaterAdvantage> awayTeam;

    public List<SkaterAdvantage> getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(final List<SkaterAdvantage> homeTeam) {
        this.homeTeam = homeTeam;
    }

    public List<SkaterAdvantage> getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(final List<SkaterAdvantage> awayTeam) {
        this.awayTeam = awayTeam;
    }

    @Override
    public String toString() {
        return "SkaterAdvantages{" +
                "homeTeam=" + homeTeam +
                ", awayTeam=" + awayTeam +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkaterAdvantages that = (SkaterAdvantages) o;
        return Objects.equals(homeTeam, that.homeTeam) &&
                Objects.equals(awayTeam, that.awayTeam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeTeam, awayTeam);
    }
}
