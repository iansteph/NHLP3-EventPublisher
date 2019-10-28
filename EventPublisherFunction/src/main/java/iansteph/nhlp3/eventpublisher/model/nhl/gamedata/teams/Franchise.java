package iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Franchise {

    private int franchiseId;
    private String teamName;
    private String link;

    public int getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(final int franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Franchise{" +
                "franchiseId=" + franchiseId +
                ", teamName='" + teamName + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Franchise franchise = (Franchise) o;
        return franchiseId == franchise.franchiseId &&
                Objects.equals(teamName, franchise.teamName) &&
                Objects.equals(link, franchise.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(franchiseId, teamName, link);
    }
}
