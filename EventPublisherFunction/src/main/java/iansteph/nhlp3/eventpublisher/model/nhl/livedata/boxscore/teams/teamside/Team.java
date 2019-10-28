package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    private int id;
    private String name;
    private String link;
    private String abbreviation;
    private String triCode;

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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getTriCode() {
        return triCode;
    }

    public void setTriCode(final String triCode) {
        this.triCode = triCode;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", triCode='" + triCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id &&
                Objects.equals(name, team.name) &&
                Objects.equals(link, team.link) &&
                Objects.equals(abbreviation, team.abbreviation) &&
                Objects.equals(triCode, team.triCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link, abbreviation, triCode);
    }
}
