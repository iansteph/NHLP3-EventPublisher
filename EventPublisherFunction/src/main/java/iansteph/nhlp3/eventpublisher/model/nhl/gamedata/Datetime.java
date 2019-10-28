package iansteph.nhlp3.eventpublisher.model.nhl.gamedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Datetime {

    private ZonedDateTime dateTime;
    private ZonedDateTime endDateTime;

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(final ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(final ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public String toString() {
        return "Datetime{" +
                "dateTime=" + dateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Datetime datetime = (Datetime) o;
        return Objects.equals(dateTime, datetime.dateTime) &&
                Objects.equals(endDateTime, datetime.endDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, endDateTime);
    }
}
