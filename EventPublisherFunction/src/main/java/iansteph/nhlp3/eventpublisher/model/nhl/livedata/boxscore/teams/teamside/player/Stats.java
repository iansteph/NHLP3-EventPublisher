package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {

    private String timeOnIce;
    private int assists;
    private int goals;
    private int shots;
    private int powerPlayGoals;
    private int powerPlayAssists;
    private int penaltyMinutes;
    private double faceOffPct;
    private int faceOffWins;
    private int faceoffTaken;
    private int takeaways;
    private int giveaways;
    private int shortHandedGoals;
    private int shortHandedAssists;
    private int blocked;
    private int plusMinus;
    private String evenTimeOnIce;
    private String powerPlayTimeOnIce;
    private String shortHandedTimeOnIce;

    public String getTimeOnIce() {
        return timeOnIce;
    }

    public void setTimeOnIce(final String timeOnIce) {
        this.timeOnIce = timeOnIce;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(final int assists) {
        this.assists = assists;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(final int goals) {
        this.goals = goals;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(final int shots) {
        this.shots = shots;
    }

    public int getPowerPlayGoals() {
        return powerPlayGoals;
    }

    public void setPowerPlayGoals(final int powerPlayGoals) {
        this.powerPlayGoals = powerPlayGoals;
    }

    public int getPowerPlayAssists() {
        return powerPlayAssists;
    }

    public void setPowerPlayAssists(final int powerPlayAssists) {
        this.powerPlayAssists = powerPlayAssists;
    }

    public int getPenaltyMinutes() {
        return penaltyMinutes;
    }

    public void setPenaltyMinutes(final int penaltyMinutes) {
        this.penaltyMinutes = penaltyMinutes;
    }

    public double getFaceOffPct() {
        return faceOffPct;
    }

    public void setFaceOffPct(final double faceOffPct) {
        this.faceOffPct = faceOffPct;
    }

    public int getFaceOffWins() {
        return faceOffWins;
    }

    public void setFaceOffWins(final int faceOffWins) {
        this.faceOffWins = faceOffWins;
    }

    public int getFaceoffTaken() {
        return faceoffTaken;
    }

    public void setFaceoffTaken(final int faceoffTaken) {
        this.faceoffTaken = faceoffTaken;
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

    public int getShortHandedGoals() {
        return shortHandedGoals;
    }

    public void setShortHandedGoals(final int shortHandedGoals) {
        this.shortHandedGoals = shortHandedGoals;
    }

    public int getShortHandedAssists() {
        return shortHandedAssists;
    }

    public void setShortHandedAssists(final int shortHandedAssists) {
        this.shortHandedAssists = shortHandedAssists;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(final int blocked) {
        this.blocked = blocked;
    }

    public int getPlusMinus() {
        return plusMinus;
    }

    public void setPlusMinus(final int plusMinus) {
        this.plusMinus = plusMinus;
    }

    public String getEvenTimeOnIce() {
        return evenTimeOnIce;
    }

    public void setEvenTimeOnIce(final String evenTimeOnIce) {
        this.evenTimeOnIce = evenTimeOnIce;
    }

    public String getPowerPlayTimeOnIce() {
        return powerPlayTimeOnIce;
    }

    public void setPowerPlayTimeOnIce(final String powerPlayTimeOnIce) {
        this.powerPlayTimeOnIce = powerPlayTimeOnIce;
    }

    public String getShortHandedTimeOnIce() {
        return shortHandedTimeOnIce;
    }

    public void setShortHandedTimeOnIce(final String shortHandedTimeOnIce) {
        this.shortHandedTimeOnIce = shortHandedTimeOnIce;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "timeOnIce='" + timeOnIce + '\'' +
                ", assists=" + assists +
                ", goals=" + goals +
                ", shots=" + shots +
                ", powerPlayGoals=" + powerPlayGoals +
                ", powerPlayAssists=" + powerPlayAssists +
                ", penaltyMinutes=" + penaltyMinutes +
                ", faceOffPct=" + faceOffPct +
                ", faceOffWins=" + faceOffWins +
                ", faceoffTaken=" + faceoffTaken +
                ", takeaways=" + takeaways +
                ", giveaways=" + giveaways +
                ", shortHandedGoals=" + shortHandedGoals +
                ", shortHandedAssists=" + shortHandedAssists +
                ", blocked=" + blocked +
                ", plusMinus=" + plusMinus +
                ", evenTimeOnIce='" + evenTimeOnIce + '\'' +
                ", powerPlayTimeOnIce='" + powerPlayTimeOnIce + '\'' +
                ", shortHandedTimeOnIce='" + shortHandedTimeOnIce + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stats stats = (Stats) o;
        return assists == stats.assists &&
                goals == stats.goals &&
                shots == stats.shots &&
                powerPlayGoals == stats.powerPlayGoals &&
                powerPlayAssists == stats.powerPlayAssists &&
                penaltyMinutes == stats.penaltyMinutes &&
                Double.compare(stats.faceOffPct, faceOffPct) == 0 &&
                faceOffWins == stats.faceOffWins &&
                faceoffTaken == stats.faceoffTaken &&
                takeaways == stats.takeaways &&
                giveaways == stats.giveaways &&
                shortHandedGoals == stats.shortHandedGoals &&
                shortHandedAssists == stats.shortHandedAssists &&
                blocked == stats.blocked &&
                plusMinus == stats.plusMinus &&
                Objects.equals(timeOnIce, stats.timeOnIce) &&
                Objects.equals(evenTimeOnIce, stats.evenTimeOnIce) &&
                Objects.equals(powerPlayTimeOnIce, stats.powerPlayTimeOnIce) &&
                Objects.equals(shortHandedTimeOnIce, stats.shortHandedTimeOnIce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeOnIce, assists, goals, shots, powerPlayGoals, powerPlayAssists, penaltyMinutes, faceOffPct, faceOffWins,
                faceoffTaken, takeaways, giveaways, shortHandedGoals, shortHandedAssists, blocked, plusMinus, evenTimeOnIce,
                powerPlayTimeOnIce, shortHandedTimeOnIce);
    }
}