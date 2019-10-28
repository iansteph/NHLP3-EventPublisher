package iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Division {

    private int id;
    private String name;
    private String nameShort;
    private String link;
    private String abbreviation;

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

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(final String nameShort) {
        this.nameShort = nameShort;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return "Division{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nameShort='" + nameShort + '\'' +
                ", link='" + link + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Division division = (Division) o;
        return id == division.id &&
                Objects.equals(name, division.name) &&
                Objects.equals(nameShort, division.nameShort) &&
                Objects.equals(link, division.link) &&
                Objects.equals(abbreviation, division.abbreviation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, nameShort, link, abbreviation);
    }
}
