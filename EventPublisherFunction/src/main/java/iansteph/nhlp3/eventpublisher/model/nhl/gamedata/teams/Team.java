package iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    private int id;
    private String name;
    private String link;
    private TeamVenue teamVenue;
    private String abbreviation;
    private String triCode;
    private String teamName;
    private String locationName;
    private int firstYearOfPlay;
    private Division division;
    private Conference conference;
    private Franchise franchise;
    private String shortName;
    private String officialSiteUrl;
    private int franchiseId;
    private boolean active;

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

    public TeamVenue getTeamVenue() {
        return teamVenue;
    }

    public void setTeamVenue(final TeamVenue teamVenue) {
        this.teamVenue = teamVenue;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(final String locationName) {
        this.locationName = locationName;
    }

    public int getFirstYearOfPlay() {
        return firstYearOfPlay;
    }

    public void setFirstYearOfPlay(final int firstYearOfPlay) {
        this.firstYearOfPlay = firstYearOfPlay;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(final Division division) {
        this.division = division;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(final Conference conference) {
        this.conference = conference;
    }

    public Franchise getFranchise() {
        return franchise;
    }

    public void setFranchise(final Franchise franchise) {
        this.franchise = franchise;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public String getOfficialSiteUrl() {
        return officialSiteUrl;
    }

    public void setOfficialSiteUrl(final String officialSiteUrl) {
        this.officialSiteUrl = officialSiteUrl;
    }

    public int getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(final int franchiseId) {
        this.franchiseId = franchiseId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", teamVenue=" + teamVenue +
                ", abbreviation='" + abbreviation + '\'' +
                ", triCode='" + triCode + '\'' +
                ", teamName='" + teamName + '\'' +
                ", locationName='" + locationName + '\'' +
                ", firstYearOfPlay=" + firstYearOfPlay +
                ", division=" + division +
                ", conference=" + conference +
                ", franchise=" + franchise +
                ", shortName='" + shortName + '\'' +
                ", officialSiteUrl='" + officialSiteUrl + '\'' +
                ", franchiseId=" + franchiseId +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id == team.id &&
                firstYearOfPlay == team.firstYearOfPlay &&
                franchiseId == team.franchiseId &&
                active == team.active &&
                Objects.equals(name, team.name) &&
                Objects.equals(link, team.link) &&
                Objects.equals(teamVenue, team.teamVenue) &&
                Objects.equals(abbreviation, team.abbreviation) &&
                Objects.equals(triCode, team.triCode) &&
                Objects.equals(teamName, team.teamName) &&
                Objects.equals(locationName, team.locationName) &&
                Objects.equals(division, team.division) &&
                Objects.equals(conference, team.conference) &&
                Objects.equals(franchise, team.franchise) &&
                Objects.equals(shortName, team.shortName) &&
                Objects.equals(officialSiteUrl, team.officialSiteUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link, teamVenue, abbreviation, triCode, teamName, locationName, firstYearOfPlay, division, conference,
                franchise, shortName, officialSiteUrl, franchiseId, active);
    }
}
