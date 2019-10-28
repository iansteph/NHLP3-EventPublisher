package iansteph.nhlp3.eventpublisher.model.nhl.gamedata.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameType {

    @JsonProperty("PR") PRESEASON,
    @JsonProperty("R") REGULAR_SEASON,
    @JsonProperty("P") PLAYOFFS,
    @JsonProperty("A") ALL_STAR,
    @JsonProperty("O") OLYMPIC,
    @JsonProperty("E") EXHIBITION,
    @JsonProperty("WCOH_EXH") WORLD_CUP_OF_HOCKEY_EXHIBITION,
    @JsonProperty("WCOH_PRELIM") WORLD_CUP_OF_HOCKEY_PRELIMINARY,
    @JsonProperty("WCOH_FINAL") WORLD_CUP_OF_HOCKEY_FINALS

}
