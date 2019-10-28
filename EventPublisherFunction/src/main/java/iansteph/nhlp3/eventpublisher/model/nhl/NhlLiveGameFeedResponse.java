package iansteph.nhlp3.eventpublisher.model.nhl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.GameData;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.LiveData;
import iansteph.nhlp3.eventpublisher.model.nhl.metadata.MetaData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NhlLiveGameFeedResponse {

    private String copyright;
    private int gamePk;
    private String link;
    private MetaData metaData;
    private GameData gameData;
    private LiveData liveData;

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(final String copyright) {
        this.copyright = copyright;
    }

    public int getGamePk() {
        return gamePk;
    }

    public void setGamePk(final int gamePk) {
        this.gamePk = gamePk;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(final MetaData metaData) {
        this.metaData = metaData;
    }

    public GameData getGameData() {
        return gameData;
    }

    public void setGameData(final GameData gameData) {
        this.gameData = gameData;
    }

    public LiveData getLiveData() {
        return liveData;
    }

    public void setLiveData(final LiveData liveData) {
        this.liveData = liveData;
    }

    @Override
    public String toString() {
        return "NhlLiveGameFeedResponse{" +
                "copyright='" + copyright + '\'' +
                ", gamePk=" + gamePk +
                ", link='" + link + '\'' +
                ", metaData=" + metaData +
                ", gameData=" + gameData +
                ", liveData=" + liveData +
                '}';
    }
}
