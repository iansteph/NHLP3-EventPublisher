package iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conference {

    private int id;
    private String name;
    private String link;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "Conference{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conference that = (Conference) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link);
    }
}
