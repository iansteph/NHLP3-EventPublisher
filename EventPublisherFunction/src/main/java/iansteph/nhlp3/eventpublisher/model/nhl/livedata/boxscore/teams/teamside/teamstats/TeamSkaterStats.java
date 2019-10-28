package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside.teamstats;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamSkaterStats {

    private int goals;
    private int pim;
    private int shots;
    private String powerPlayPercentage;
    private double powerPlayGoals;
    private double powerPlayOpportunites;
    private String faceOffWinPercentage;
    private int blocked;
    private int takeaways;
    private int giveaways;
    private int hits;

    public int getGoals() {
        return goals;
    }

    public void setGoals(final int goals) {
        this.goals = goals;
    }

    public int getPim() {
        return pim;
    }

    public void setPim(final int pim) {
        this.pim = pim;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(final int shots) {
        this.shots = shots;
    }

    public String getPowerPlayPercentage() {
        return powerPlayPercentage;
    }

    public void setPowerPlayPercentage(final String powerPlayPercentage) {
        this.powerPlayPercentage = powerPlayPercentage;
    }

    public double getPowerPlayGoals() {
        return powerPlayGoals;
    }

    public void setPowerPlayGoals(final double powerPlayGoals) {
        this.powerPlayGoals = powerPlayGoals;
    }

    public double getPowerPlayOpportunites() {
        return powerPlayOpportunites;
    }

    public void setPowerPlayOpportunites(final double powerPlayOpportunites) {
        this.powerPlayOpportunites = powerPlayOpportunites;
    }

    public String getFaceOffWinPercentage() {
        return faceOffWinPercentage;
    }

    public void setFaceOffWinPercentage(final String faceOffWinPercentage) {
        this.faceOffWinPercentage = faceOffWinPercentage;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(final int blocked) {
        this.blocked = blocked;
    }

    public int getTakeaways() {
        return takeaways;
    }

    public void setTakeaways(final int takeaways) {
        this.takeaways = takeaways;
    }

    public int getGiveaways() {
        return giveaways;
    }

    public void setGiveaways(final int giveaways) {
        this.giveaways = giveaways;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(final int hits) {
        this.hits = hits;
    }

    @Override
    public String toString() {
        return "TeamSkaterStats{" +
                "goals=" + goals +
                ", pim=" + pim +
                ", shots=" + shots +
                ", powerPlayPercentage='" + powerPlayPercentage + '\'' +
                ", powerPlayGoals=" + powerPlayGoals +
                ", powerPlayOpportunites=" + powerPlayOpportunites +
                ", faceOffWinPercentage='" + faceOffWinPercentage + '\'' +
                ", blocked=" + blocked +
                ", takeaways=" + takeaways +
                ", giveaways=" + giveaways +
                ", hits=" + hits +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamSkaterStats that = (TeamSkaterStats) o;
        return goals == that.goals &&
                pim == that.pim &&
                shots == that.shots &&
                Double.compare(that.powerPlayGoals, powerPlayGoals) == 0 &&
                Double.compare(that.powerPlayOpportunites, powerPlayOpportunites) == 0 &&
                blocked == that.blocked &&
                takeaways == that.takeaways &&
                giveaways == that.giveaways &&
                hits == that.hits &&
                Objects.equals(powerPlayPercentage, that.powerPlayPercentage) &&
                Objects.equals(faceOffWinPercentage, that.faceOffWinPercentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goals, pim, shots, powerPlayPercentage, powerPlayGoals, powerPlayOpportunites, faceOffWinPercentage, blocked,
                takeaways, giveaways, hits);
    }
}