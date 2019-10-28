package iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams.teamvenue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeZone {

    private String id;
    private int offset;
    private String tz;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(final String tz) {
        this.tz = tz;
    }

    @Override
    public String toString() {
        return "TimeZone{" +
                "id='" + id + '\'' +
                ", offset=" + offset +
                ", tz='" + tz + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeZone timeZone = (TimeZone) o;
        return offset == timeZone.offset &&
                Objects.equals(id, timeZone.id) &&
                Objects.equals(tz, timeZone.tz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, offset, tz);
    }
}
