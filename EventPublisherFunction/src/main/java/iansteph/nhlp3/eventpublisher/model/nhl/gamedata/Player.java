package iansteph.nhlp3.eventpublisher.model.nhl.gamedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.common.Handedness;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.player.PlayerTeam;
import iansteph.nhlp3.eventpublisher.model.nhl.common.Position;

import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {

    private int id;
    private String fullName;
    private String link;
    private String firstName;
    private String lastName;
    private int primaryNumber;
    private LocalDate birthDate;
    private int currentAge;
    private String birthCity;
    private String birthStateProvince;
    private String birthCountry;
    private String nationality;
    private String height;
    private int weight;
    private boolean active;
    private boolean alternateCaptain;
    private boolean captain;
    private boolean rookie;
    private Handedness shootsCatches;
    private PlayerTeam currentTeam;
    private Position primaryPosition;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public int getPrimaryNumber() {
        return primaryNumber;
    }

    public void setPrimaryNumber(final int primaryNumber) {
        this.primaryNumber = primaryNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(final int currentAge) {
        this.currentAge = currentAge;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(final String birthCity) {
        this.birthCity = birthCity;
    }

    public String getBirthStateProvince() {
        return birthStateProvince;
    }

    public void setBirthStateProvince(final String birthStateProvince) {
        this.birthStateProvince = birthStateProvince;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(final String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(final String nationality) {
        this.nationality = nationality;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(final String height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(final int weight) {
        this.weight = weight;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public boolean isAlternateCaptain() {
        return alternateCaptain;
    }

    public void setAlternateCaptain(final boolean alternateCaptain) {
        this.alternateCaptain = alternateCaptain;
    }

    public boolean isCaptain() {
        return captain;
    }

    public void setCaptain(final boolean captain) {
        this.captain = captain;
    }

    public boolean isRookie() {
        return rookie;
    }

    public void setRookie(final boolean rookie) {
        this.rookie = rookie;
    }

    public Handedness getShootsCatches() {
        return shootsCatches;
    }

    public void setShootsCatches(final Handedness shootsCatches) {
        this.shootsCatches = shootsCatches;
    }

    public PlayerTeam getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(final PlayerTeam currentTeam) {
        this.currentTeam = currentTeam;
    }

    public Position getPrimaryPosition() {
        return primaryPosition;
    }

    public void setPrimaryPosition(final Position primaryPosition) {
        this.primaryPosition = primaryPosition;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", link='" + link + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", primaryNumber=" + primaryNumber +
                ", birthDate=" + birthDate +
                ", currentAge=" + currentAge +
                ", birthCity='" + birthCity + '\'' +
                ", birthStateProvince='" + birthStateProvince + '\'' +
                ", birthCountry='" + birthCountry + '\'' +
                ", nationality='" + nationality + '\'' +
                ", height='" + height + '\'' +
                ", weight=" + weight +
                ", active=" + active +
                ", alternateCaptain=" + alternateCaptain +
                ", captain=" + captain +
                ", rookie=" + rookie +
                ", shootsCatches=" + shootsCatches +
                ", currentTeam=" + currentTeam +
                ", primaryPosition=" + primaryPosition +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id &&
                primaryNumber == player.primaryNumber &&
                currentAge == player.currentAge &&
                weight == player.weight &&
                active == player.active &&
                alternateCaptain == player.alternateCaptain &&
                captain == player.captain &&
                rookie == player.rookie &&
                Objects.equals(fullName, player.fullName) &&
                Objects.equals(link, player.link) &&
                Objects.equals(firstName, player.firstName) &&
                Objects.equals(lastName, player.lastName) &&
                Objects.equals(birthDate, player.birthDate) &&
                Objects.equals(birthCity, player.birthCity) &&
                Objects.equals(birthStateProvince, player.birthStateProvince) &&
                Objects.equals(birthCountry, player.birthCountry) &&
                Objects.equals(nationality, player.nationality) &&
                Objects.equals(height, player.height) &&
                shootsCatches == player.shootsCatches &&
                Objects.equals(currentTeam, player.currentTeam) &&
                Objects.equals(primaryPosition, player.primaryPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, link, firstName, lastName, primaryNumber, birthDate, currentAge, birthCity, birthStateProvince,
                birthCountry, nationality, height, weight, active, alternateCaptain, captain, rookie, shootsCatches, currentTeam,
                primaryPosition);
    }
}
