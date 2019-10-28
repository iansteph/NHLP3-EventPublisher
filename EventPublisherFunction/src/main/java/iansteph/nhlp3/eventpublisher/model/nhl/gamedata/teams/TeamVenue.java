package iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams.teamvenue.TimeZone;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamVenue {

    private String name;
    private String link;
    private String city;
    private TimeZone timeZone;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "TeamVenue{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", city='" + city + '\'' +
                ", timeZone=" + timeZone +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamVenue teamVenue = (TeamVenue) o;
        return Objects.equals(name, teamVenue.name) &&
                Objects.equals(link, teamVenue.link) &&
                Objects.equals(city, teamVenue.city) &&
                Objects.equals(timeZone, teamVenue.timeZone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link, city, timeZone);
    }
}
