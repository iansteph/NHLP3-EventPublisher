package iansteph.nhlp3.eventpublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.Result;
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

    public NhlLiveGameFeedResponse getPlayByPlayResponseFromTestResource() throws IOException {

        final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        final File playByPlayResponseTestResourceFile = new File("src/test/resources/play-by-play-response.json");
        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = objectMapper.readValue(playByPlayResponseTestResourceFile,
                NhlLiveGameFeedResponse.class);
        return nhlLiveGameFeedResponse;
    }

    public NhlLiveGameFeedResponse createNhlLiveGameFeedResponse() {

        final NhlLiveGameFeedResponse liveGameFeedResponse = new NhlLiveGameFeedResponse();
        liveGameFeedResponse.setGamePk(GameId);
        // GameData
        final GameData gameData = new GameData();

        // -- Teams
        final Teams teams = new Teams();
        final Team homeTeam = new Team();
        homeTeam.setId(1);
        teams.setHome(homeTeam);
        final Team awayTeam = new Team();
        awayTeam.setId(2);
        teams.setAway(awayTeam);

        // -- Status
        final Status status = new Status();
        status.setAbstractGameState("Final");

        gameData.setTeams(teams);
        gameData.setStatus(status);
        liveGameFeedResponse.setGameData(gameData);


        // LiveData
        final LiveData liveData = new LiveData();

        // -- Plays
        final Plays plays = new Plays();

        // -- -- CurrentPlay
        final Play currentPlay = new Play();

        // -- -- -- About
        final About about = new About();
        about.setEventIdx(1);

        // -- -- -- Result
        final Result result = new Result();
        result.setEventCode("TEST2");

        currentPlay.setAbout(about);
        currentPlay.setResult(result);
        plays.setCurrentPlay(currentPlay);

        // -- -- AllPlays
        final List<Play> allPlays = new ArrayList<>();

        final Play firstPlay = new Play();
        final About firstAbout = new About();
        firstAbout.setEventIdx(0);
        firstPlay.setAbout(firstAbout);
        final Result firstResult = new Result();
        firstResult.setEventCode("TEST1");
        firstPlay.setResult(firstResult);

        allPlays.add(firstPlay);
        allPlays.add(currentPlay);
        plays.setAllPlays(allPlays);

        liveData.setPlays(plays);
        liveGameFeedResponse.setLiveData(liveData);
        return liveGameFeedResponse;
    }

    // Create this class that extends ObjectMapper, because ObjectMapper.writeValueAsString() throws unchecked exception & does not compile
    protected class SafeObjectMapper extends ObjectMapper {

        @Override
        public String writeValueAsString(final Object value) {
            return "Hello, World!";
        }
    }
}
