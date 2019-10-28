package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.teams.teamside;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OnIcePlus {

    private int playerId;
    private int shiftDuration;
    private int stamina;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }

    public int getShiftDuration() {
        return shiftDuration;
    }

    public void setShiftDuration(final int shiftDuration) {
        this.shiftDuration = shiftDuration;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(final int stamina) {
        this.stamina = stamina;
    }

    @Override
    public String toString() {
        return "OnIcePlus{" +
                "playerId=" + playerId +
                ", shiftDuration=" + shiftDuration +
                ", stamina=" + stamina +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnIcePlus onIcePlus = (OnIcePlus) o;
        return playerId == onIcePlus.playerId &&
                shiftDuration == onIcePlus.shiftDuration &&
                stamina == onIcePlus.stamina;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, shiftDuration, stamina);
    }
}
