package iansteph.nhlp3.eventpublisher.model.request;

import java.util.Objects;

public class EventPublisherRequest {

    private int gameId;

    public EventPublisherRequest() {}

    public EventPublisherRequest(final int gameId) {
        this.gameId = gameId;
    }

    public int getGameId() {
        return this.gameId;
    }

    public void setGameId(final int gameId) {
        this.gameId = gameId;
    }

    @Override
    public String toString() {
        return "EventPublisherRequest{" +
                "gameId=" + gameId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventPublisherRequest that = (EventPublisherRequest) o;
        return gameId == that.gameId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }
}
