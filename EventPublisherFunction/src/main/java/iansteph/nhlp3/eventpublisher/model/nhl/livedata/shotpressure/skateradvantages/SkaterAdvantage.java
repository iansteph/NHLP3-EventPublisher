package iansteph.nhlp3.eventpublisher.model.nhl.livedata.shotpressure.skateradvantages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SkaterAdvantage {

    private int start;
    private int end;
    private int skaterDifference;

    public int getStart() {
        return start;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(final int end) {
        this.end = end;
    }

    public int getSkaterDifference() {
        return skaterDifference;
    }

    public void setSkaterDifference(final int skaterDifference) {
        this.skaterDifference = skaterDifference;
    }

    @Override
    public String toString() {
        return "SkaterAdvantage{" +
                "start=" + start +
                ", end=" + end +
                ", skaterDifference=" + skaterDifference +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkaterAdvantage that = (SkaterAdvantage) o;
        return start == that.start &&
                end == that.end &&
                skaterDifference == that.skaterDifference;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, skaterDifference);
    }
}
