package iansteph.nhlp3.eventpublisher;

import com.google.common.io.Files;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.GameData;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.Status;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.Teams;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams.Team;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.LiveData;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.Plays;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.About;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UnitTestBase {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OBJECTS                                                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    protected final int GameId = 2019020054;

    protected final EventPublisherRequest EventPublisherRequest = new EventPublisherRequest(GameId);

    protected final NhlLiveGameFeedResponse NhlLiveGameFeedResponse = createNhlLiveGameFeedResponse();


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS                                                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getTestPlayByPlayResponseResourceAsString() throws IOException {

        return getTestResourceAsString("src/test/resources/play-by-play-response.json");
    }

    public String getTestResourceAsString(final String filename) throws IOException {

        return Files.asCharSource(new File(filename), StandardCharsets.UTF_8).read();
    }

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
        liveGameFeedResponse.setGamePk(GameId);
        return liveGameFeedResponse;
    }
}
