package iansteph.nhlp3.eventpublisher.model.nhl.livedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.decisions.Player;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Decisions {

    private Player winner;
    private Player loser;
    private Player firstStar;
    private Player secondStar;
    private Player thirdStar;

    public Player getWinner() {
        return winner;
    }

    public void setWinner(final Player winner) {
        this.winner = winner;
    }

    public Player getLoser() {
        return loser;
    }

    public void setLoser(final Player loser) {
        this.loser = loser;
    }

    public Player getFirstStar() {
        return firstStar;
    }

    public void setFirstStar(final Player firstStar) {
        this.firstStar = firstStar;
    }

    public Player getSecondStar() {
        return secondStar;
    }

    public void setSecondStar(final Player secondStar) {
        this.secondStar = secondStar;
    }

    public Player getThirdStar() {
        return thirdStar;
    }

    public void setThirdStar(final Player thirdStar) {
        this.thirdStar = thirdStar;
    }

    @Override
    public String toString() {
        return "Decisions{" +
                "winner=" + winner +
                ", loser=" + loser +
                ", firstStar=" + firstStar +
                ", secondStar=" + secondStar +
                ", thirdStar=" + thirdStar +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Decisions decisions = (Decisions) o;
        return Objects.equals(winner, decisions.winner) &&
                Objects.equals(loser, decisions.loser) &&
                Objects.equals(firstStar, decisions.firstStar) &&
                Objects.equals(secondStar, decisions.secondStar) &&
                Objects.equals(thirdStar, decisions.thirdStar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winner, loser, firstStar, secondStar, thirdStar);
    }
}
