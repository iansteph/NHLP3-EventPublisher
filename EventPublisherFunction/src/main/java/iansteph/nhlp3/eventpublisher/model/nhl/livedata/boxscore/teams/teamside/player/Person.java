package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.common.Handedness;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {

    private int id;
    private String fullName;
    private String link;
    private Handedness shootsCatches;
    private String rosterStatus;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public Handedness getShootsCatches() {
        return shootsCatches;
    }

    public void setShootsCatches(final Handedness shootsCatches) {
        this.shootsCatches = shootsCatches;
    }

    public String getRosterStatus() {
        return rosterStatus;
    }

    public void setRosterStatus(final String rosterStatus) {
        this.rosterStatus = rosterStatus;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", link='" + link + '\'' +
                ", shootsCatches=" + shootsCatches +
                ", rosterStatus='" + rosterStatus + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id &&
                Objects.equals(fullName, person.fullName) &&
                Objects.equals(link, person.link) &&
                shootsCatches == person.shootsCatches &&
                Objects.equals(rosterStatus, person.rosterStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, link, shootsCatches, rosterStatus);
    }
}
