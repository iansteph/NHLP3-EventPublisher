package iansteph.nhlp3.eventpublisher.model.nhl.gamedata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {

    private String abstractGameState;
    private String codedGameState;
    private String detailedState;
    private String statusCode;
    private boolean startingTimeTBD;

    public String getAbstractGameState() {
        return abstractGameState;
    }

    public void setAbstractGameState(final String abstractGameState) {
        this.abstractGameState = abstractGameState;
    }

    public String getCodedGameState() {
        return codedGameState;
    }

    public void setCodedGameState(final String codedGameState) {
        this.codedGameState = codedGameState;
    }

    public String getDetailedState() {
        return detailedState;
    }

    public void setDetailedState(final String detailedState) {
        this.detailedState = detailedState;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isStartingTimeTBD() {
        return startingTimeTBD;
    }

    public void setStartingTimeTBD(final boolean startingTimeTBD) {
        this.startingTimeTBD = startingTimeTBD;
    }

    public boolean isGameEnded() {
        return this.abstractGameState.toLowerCase().equals("final");
    }

    @Override
    public String toString() {
        return "Status{" +
                "abstractGameState='" + abstractGameState + '\'' +
                ", codedGameState='" + codedGameState + '\'' +
                ", detailedState='" + detailedState + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", startingTimeTBD=" + startingTimeTBD +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return startingTimeTBD == status.startingTimeTBD &&
                Objects.equals(abstractGameState, status.abstractGameState) &&
                Objects.equals(codedGameState, status.codedGameState) &&
                Objects.equals(detailedState, status.detailedState) &&
                Objects.equals(statusCode, status.statusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abstractGameState, codedGameState, detailedState, statusCode, startingTimeTBD);
    }
}
