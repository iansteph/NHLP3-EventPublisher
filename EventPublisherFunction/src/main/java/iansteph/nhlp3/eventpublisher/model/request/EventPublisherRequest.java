package iansteph.nhlp3.eventpublisher.model.request;

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
}
