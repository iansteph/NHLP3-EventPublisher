package iansteph.nhlp3.eventpublisher.model.nhl.gamedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.game.GameType;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    private int pk;
    private String season;
    private GameType type;

    public int getPk() {
        return pk;
    }

    public void setPk(final int pk) {
        this.pk = pk;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(final String season) {
        this.season = season;
    }

    public GameType getType() {
        return type;
    }

    public void setType(final GameType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Game{" +
                "pk=" + pk +
                ", season='" + season + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return pk == game.pk &&
                Objects.equals(season, game.season) &&
                type == game.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, season, type);
    }
}
