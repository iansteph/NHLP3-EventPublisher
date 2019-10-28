package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.teams.teamside;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamSideTeam {

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
        return "TeamSideTeam{" +
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
        TeamSideTeam that = (TeamSideTeam) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(link, that.link) &&
                Objects.equals(abbreviation, that.abbreviation) &&
                Objects.equals(triCode, that.triCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link, abbreviation, triCode);
    }
}
