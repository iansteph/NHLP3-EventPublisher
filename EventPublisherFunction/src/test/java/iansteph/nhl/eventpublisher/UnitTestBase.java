package iansteph.nhl.eventpublisher;

import iansteph.nhl.eventpublisher.handler.EventPublisherRequest;
import iansteph.nhl.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhl.eventpublisher.model.nhl.gamedata.GameData;
import iansteph.nhl.eventpublisher.model.nhl.gamedata.Status;
import iansteph.nhl.eventpublisher.model.nhl.gamedata.Teams;
import iansteph.nhl.eventpublisher.model.nhl.gamedata.teams.Team;
import iansteph.nhl.eventpublisher.model.nhl.livedata.LiveData;
import iansteph.nhl.eventpublisher.model.nhl.livedata.Plays;
import iansteph.nhl.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhl.eventpublisher.model.nhl.livedata.plays.play.About;

import java.util.ArrayList;
import java.util.List;

public class UnitTestBase {

    protected final EventPublisherRequest EventPublisherRequest = new EventPublisherRequest(999);

    protected final NhlLiveGameFeedResponse NhlLiveGameFeedResponse = createNhlLiveGameFeedResponse();

    private NhlLiveGameFeedResponse createNhlLiveGameFeedResponse() {
        final NhlLiveGameFeedResponse liveGameFeedResponse = new NhlLiveGameFeedResponse();
        final Teams teams = new Teams();
        final Team homeTeam = new Team();
        homeTeam.setId(1);
        teams.setHome(homeTeam);
        final Team awayTeam = new Team();
        awayTeam.setId(2);
        teams.setAway(awayTeam);
        final GameData gameData = new GameData();
        gameData.setTeams(teams);
        final Status status = new Status();
        status.setAbstractGameState("Final");
        gameData.setStatus(status);
        liveGameFeedResponse.setGameData(gameData);
        final About about = new About();
        about.setEventIdx(1);
        final Play currentPlay = new Play();
        currentPlay.setAbout(about);
        final Plays plays = new Plays();
        plays.setCurrentPlay(currentPlay);
        final List<Play> allPlays = new ArrayList<>();
        allPlays.add(new Play());
        allPlays.add(new Play());
        plays.setAllPlays(allPlays);
        final LiveData liveData = new LiveData();
        liveData.setPlays(plays);
        liveGameFeedResponse.setLiveData(liveData);
        return liveGameFeedResponse;
    }
}
