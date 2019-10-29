package iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Period {

    private int startIndex;
    private List<Integer> plays;
    private int endIndex;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(final int startIndex) {
        this.startIndex = startIndex;
    }

    public List<Integer> getPlays() {
        return plays;
    }

    public void setPlays(final List<Integer> plays) {
        this.plays = plays;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(final int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public String toString() {
        return "Period{" +
                "startIndex=" + startIndex +
                ", plays=" + plays +
                ", endIndex=" + endIndex +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return startIndex == period.startIndex &&
                endIndex == period.endIndex &&
                Objects.equals(plays, period.plays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startIndex, plays, endIndex);
    }
}
