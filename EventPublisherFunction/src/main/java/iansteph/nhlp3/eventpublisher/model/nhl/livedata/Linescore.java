package iansteph.nhlp3.eventpublisher.model.nhl.livedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.*;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Linescore {

    private int currentPeriod;
    private String currentPeriodOrdinal;
    private String currentPeriodTimeRemaining;
    private List<Period> periods;
    private ShootoutInfo shootoutInfo;
    private Teams teams;
    private String powerPlayStrength;
    private boolean hasShootout;
    private IntermissionInfo intermissionInfo;
    private PowerPlayInfo powerPlayInfo;

    public int getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(final int currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public String getCurrentPeriodOrdinal() {
        return currentPeriodOrdinal;
    }

    public void setCurrentPeriodOrdinal(final String currentPeriodOrdinal) {
        this.currentPeriodOrdinal = currentPeriodOrdinal;
    }

    public String getCurrentPeriodTimeRemaining() {
        return currentPeriodTimeRemaining;
    }

    public void setCurrentPeriodTimeRemaining(final String currentPeriodTimeRemaining) {
        this.currentPeriodTimeRemaining = currentPeriodTimeRemaining;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    public void setPeriods(final List<Period> periods) {
        this.periods = periods;
    }

    public ShootoutInfo getShootoutInfo() {
        return shootoutInfo;
    }

    public void setShootoutInfo(final ShootoutInfo shootoutInfo) {
        this.shootoutInfo = shootoutInfo;
    }

    public Teams getTeams() {
        return teams;
    }

    public void setTeams(final Teams teams) {
        this.teams = teams;
    }

    public String getPowerPlayStrength() {
        return powerPlayStrength;
    }

    public void setPowerPlayStrength(final String powerPlayStrength) {
        this.powerPlayStrength = powerPlayStrength;
    }

    public boolean isHasShootout() {
        return hasShootout;
    }

    public void setHasShootout(final boolean hasShootout) {
        this.hasShootout = hasShootout;
    }

    public IntermissionInfo getIntermissionInfo() {
        return intermissionInfo;
    }

    public void setIntermissionInfo(final IntermissionInfo intermissionInfo) {
        this.intermissionInfo = intermissionInfo;
    }

    public PowerPlayInfo getPowerPlayInfo() {
        return powerPlayInfo;
    }

    public void setPowerPlayInfo(final PowerPlayInfo powerPlayInfo) {
        this.powerPlayInfo = powerPlayInfo;
    }

    @Override
    public String toString() {
        return "Linescore{" +
                "currentPeriod=" + currentPeriod +
                ", currentPeriodOrdinal='" + currentPeriodOrdinal + '\'' +
                ", currentPeriodTimeRemaining='" + currentPeriodTimeRemaining + '\'' +
                ", periods=" + periods +
                ", shootoutInfo=" + shootoutInfo +
                ", teams=" + teams +
                ", powerPlayStrength='" + powerPlayStrength + '\'' +
                ", hasShootout=" + hasShootout +
                ", intermissionInfo=" + intermissionInfo +
                ", powerPlayInfo=" + powerPlayInfo +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Linescore linescore = (Linescore) o;
        return currentPeriod == linescore.currentPeriod &&
                hasShootout == linescore.hasShootout &&
                Objects.equals(currentPeriodOrdinal, linescore.currentPeriodOrdinal) &&
                Objects.equals(currentPeriodTimeRemaining, linescore.currentPeriodTimeRemaining) &&
                Objects.equals(periods, linescore.periods) &&
                Objects.equals(shootoutInfo, linescore.shootoutInfo) &&
                Objects.equals(teams, linescore.teams) &&
                Objects.equals(powerPlayStrength, linescore.powerPlayStrength) &&
                Objects.equals(intermissionInfo, linescore.intermissionInfo) &&
                Objects.equals(powerPlayInfo, linescore.powerPlayInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPeriod, currentPeriodOrdinal, currentPeriodTimeRemaining, periods, shootoutInfo, teams,
                powerPlayStrength, hasShootout, intermissionInfo, powerPlayInfo);
    }
}
