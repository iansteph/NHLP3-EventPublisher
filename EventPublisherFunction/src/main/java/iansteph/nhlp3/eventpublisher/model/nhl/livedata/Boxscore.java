package iansteph.nhlp3.eventpublisher.model.nhl.livedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.OfficialInfo;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.Teams;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Boxscore {

    private Teams teams;
    private List<OfficialInfo> officials;

    public Teams getTeams() {
        return teams;
    }

    public void setTeams(final Teams teams) {
        this.teams = teams;
    }

    public List<OfficialInfo> getOfficials() {
        return officials;
    }

    public void setOfficials(final List<OfficialInfo> officials) {
        this.officials = officials;
    }

    @Override
    public String toString() {
        return "Boxscore{" +
                "teams=" + teams +
                ", officials=" + officials +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boxscore boxscore = (Boxscore) o;
        return Objects.equals(teams, boxscore.teams) &&
                Objects.equals(officials, boxscore.officials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teams, officials);
    }
}
