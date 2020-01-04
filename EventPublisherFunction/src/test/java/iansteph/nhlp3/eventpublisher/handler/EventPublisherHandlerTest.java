package iansteph.nhlp3.eventpublisher.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.GameData;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.Status;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.Teams;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.teams.Team;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.LiveData;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.Plays;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.About;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.proxy.DynamoDbProxy;
import iansteph.nhlp3.eventpublisher.proxy.EventPublisherProxy;
import iansteph.nhlp3.eventpublisher.proxy.NhlPlayByPlayProxy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EventPublisherHandlerTest extends UnitTestBase {

    private final DynamoDbProxy mockDynamoDbProxy = mock(DynamoDbProxy.class);
    private final NhlPlayByPlayProxy mockNhlPlayByPlayProxy = mock(NhlPlayByPlayProxy.class);
    private final EventPublisherProxy mockEventPublisherProxy = mock(EventPublisherProxy.class);

    @Before
    public void setupMocks() {

        // Mock DynamoDbProxy
        final Map<String, AttributeValue> initialItem = new HashMap<>();
        initialItem.put("lastProcessedEventIndex", AttributeValue.builder().n("0").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(initialItem);
        final Map<String, AttributeValue> updatedItem = new HashMap<>();
        updatedItem.put("lastProcessedEventIndex", AttributeValue.builder().n("1").build());
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(), any())).thenReturn(updatedItem);

        // Mock NhlPlayByPlayProxy
        when(mockNhlPlayByPlayProxy.getPlayByPlayData(any(EventPublisherRequest.class))).thenReturn(Optional.of(NhlLiveGameFeedResponse));

        // Mock EventPublisherProxy
        when(mockEventPublisherProxy.publish(any(PlayEvent.class), anyInt(), anyInt())).thenReturn(PublishResponse.builder().build());
    }

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void test_constructor_successfully_initializes_EventPublisherHandler() {

        environmentVariables.set("Stage", "personal");

        final EventPublisherHandler eventPublisherHandler = new EventPublisherHandler();

        assertThat(eventPublisherHandler, is(notNullValue()));
    }

    @Test
    public void test_handleRequest_is_successful() {

        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(responseLastProcessedEventIndex, is(1));
        final Integer numberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(numberOfEventsPublished, is(notNullValue()));
        assertTrue(numberOfEventsPublished >= 1);
        verify(mockEventPublisherProxy, atLeast(1)).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updateNhlPlayByPlayProcessingItem(any(), any());
    }

    @Test
    public void test_handleRequest_successfully_publishes_the_first_play_by_play_event_for_a_game() {

        final About about = new About();
        about.setEventIdx(0);
        NhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().setAbout(about);
        when(mockNhlPlayByPlayProxy.getPlayByPlayData(any(EventPublisherRequest.class))).thenReturn(Optional.of(NhlLiveGameFeedResponse));
        final Map<String, AttributeValue> updatedItem = new HashMap<>();
        updatedItem.put("lastProcessedEventIndex", AttributeValue.builder().n("0").build());
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(), any())).thenReturn(updatedItem);
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(responseLastProcessedEventIndex, is(0));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(1));
        verify(mockEventPublisherProxy, times(1)).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updateNhlPlayByPlayProcessingItem(any(), any());
    }

    @Test
    public void test_handleRequest_does_not_publish_events_when_there_are_no_new_events_since_last_event_processed() {

        final About about = new About();
        about.setEventIdx(1);
        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = createNhlLiveGameFeedResponse();
        nhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().setAbout(about);
        when(mockNhlPlayByPlayProxy.getPlayByPlayData(any(EventPublisherRequest.class))).thenReturn(Optional.of(nhlLiveGameFeedResponse));
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("lastProcessedEventIndex", AttributeValue.builder().n("1").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(item);
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(), any())).thenReturn(item);
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(responseLastProcessedEventIndex, is(1));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(0));
        verify(mockEventPublisherProxy, never()).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updateNhlPlayByPlayProcessingItem(any(), any());
    }

    @Test
    public void test_handleRequest_does_not_publish_events_when_the_nhl_play_by_play_response_is_empty() {

        when(mockNhlPlayByPlayProxy.getPlayByPlayData(any(EventPublisherRequest.class))).thenReturn(Optional.empty());
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("lastProcessedEventIndex", AttributeValue.builder().n("0").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(item);
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(nullValue()));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(0));
        verify(mockEventPublisherProxy, never()).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, never()).updateNhlPlayByPlayProcessingItem(any(), any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_handleRequest_throws_IndexOutOfBoundsException_when_current_play_event_index_is_less_than_last_processed_event_index_from_dynamo() {

        final Map<String, AttributeValue> initialItem = new HashMap<>();
        initialItem.put("lastProcessedEventIndex", AttributeValue.builder().n("10").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(initialItem);
        final Map<String, AttributeValue> updatedItem = new HashMap<>();
        updatedItem.put("lastProcessedEventIndex", AttributeValue.builder().n("11").build());
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(), any())).thenReturn(updatedItem);

        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = new NhlLiveGameFeedResponse();
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
        nhlLiveGameFeedResponse.setGameData(gameData);
        final About about = new About();
        about.setEventIdx(6);
        final Play currentPlay = new Play();
        currentPlay.setAbout(about);
        final Plays plays = new Plays();
        plays.setCurrentPlay(currentPlay);
        final List<Play> allPlays = new ArrayList<>();
        allPlays.add(new Play());
        allPlays.add(new Play());
        allPlays.add(new Play());
        allPlays.add(new Play());
        allPlays.add(new Play());
        allPlays.add(new Play());
        allPlays.add(new Play());
        plays.setAllPlays(allPlays);
        final LiveData liveData = new LiveData();
        liveData.setPlays(plays);
        nhlLiveGameFeedResponse.setLiveData(liveData);
        nhlLiveGameFeedResponse.setGamePk(GameId);

        when(mockNhlPlayByPlayProxy.getPlayByPlayData(any(EventPublisherRequest.class))).thenReturn(Optional.of(nhlLiveGameFeedResponse));
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        eventPublisherHandler.handleRequest(eventPublisherRequest, null);
    }
}
