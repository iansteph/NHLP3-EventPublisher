package iansteph.nhlp3.eventpublisher.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayEvent {

    private int gamePk;
    private Play play;

    public int getGamePk() {
        return gamePk;
    }

    public void setGamePk(final int gamePk) {
        this.gamePk = gamePk;
    }

    public PlayEvent withGamePk(final int gamePk) {
        this.gamePk = gamePk;
        return this;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(final Play play) {
        this.play = play;
    }

    public PlayEvent withPlay(final Play play) {
        this.play = play;
        return this;
    }

    @Override
    public String toString() {
        return "PlayEvent{" +
                "gamePk=" + gamePk +
                ", play=" + play +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayEvent playEvent = (PlayEvent) o;
        return gamePk == playEvent.gamePk &&
                Objects.equals(play, playEvent.play);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamePk, play);
    }
}
