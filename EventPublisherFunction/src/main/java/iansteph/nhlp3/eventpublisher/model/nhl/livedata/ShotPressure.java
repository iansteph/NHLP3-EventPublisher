package iansteph.nhlp3.eventpublisher.model.nhl.livedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure.GamePressure;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure.SkaterAdvantages;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShotPressure {

    private List<GamePressure> gamePressures;
    private SkaterAdvantages skaterAdvantages;

    public List<GamePressure> getGamePressures() {
        return gamePressures;
    }

    public void setGamePressures(final List<GamePressure> gamePressures) {
        this.gamePressures = gamePressures;
    }

    public SkaterAdvantages getSkaterAdvantages() {
        return skaterAdvantages;
    }

    public void setSkaterAdvantages(final SkaterAdvantages skaterAdvantages) {
        this.skaterAdvantages = skaterAdvantages;
    }

    @Override
    public String toString() {
        return "ShotPressure{" +
                "gamePressures=" + gamePressures +
                ", skaterAdvantages=" + skaterAdvantages +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShotPressure that = (ShotPressure) o;
        return Objects.equals(gamePressures, that.gamePressures) &&
                Objects.equals(skaterAdvantages, that.skaterAdvantages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamePressures, skaterAdvantages);
    }
}
