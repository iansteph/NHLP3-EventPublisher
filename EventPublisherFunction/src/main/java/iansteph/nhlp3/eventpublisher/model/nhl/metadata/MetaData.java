package iansteph.nhlp3.eventpublisher.model.nhl.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaData {

    private int wait;
    private String timeStamp;

    public int getWait() {
        return wait;
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(final String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "MetaData{" +
                "wait=" + wait +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaData metaData = (MetaData) o;
        return wait == metaData.wait &&
                Objects.equals(timeStamp, metaData.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wait, timeStamp);
    }
}
