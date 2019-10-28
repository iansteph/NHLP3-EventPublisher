package iansteph.nhlp3.eventpublisher.model.nhl.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Handedness {

    @JsonProperty("R") RIGHT,
    @JsonProperty("L") LEFT
}
