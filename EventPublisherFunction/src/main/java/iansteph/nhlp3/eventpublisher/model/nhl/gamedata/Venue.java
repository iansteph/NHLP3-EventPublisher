package iansteph.nhlp3.eventpublisher.model.nhl.gamedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Venue {

    private int id;
    private String name;
    private String link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return id == venue.id &&
                Objects.equals(name, venue.name) &&
                Objects.equals(link, venue.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link);
    }
}
