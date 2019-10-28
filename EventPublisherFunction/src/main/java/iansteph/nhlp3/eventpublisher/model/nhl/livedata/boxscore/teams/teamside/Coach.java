package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.common.Position;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside.coach.Person;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Coach {

    private Person person;
    private Position position;

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(final Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Coach{" +
                "person=" + person +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coach coach = (Coach) o;
        return Objects.equals(person, coach.person) &&
                Objects.equals(position, coach.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, position);
    }
}
