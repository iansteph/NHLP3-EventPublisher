package iansteph.nhlp3.eventpublisher.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EventPublisherHandlerTest extends UnitTestBase {

    private final DynamoDbProxy mockDynamoDbProxy = mock(DynamoDbProxy.class);
    private final NhlPlayByPlayProxy mockNhlPlayByPlayProxy = mock(NhlPlayByPlayProxy.class);
    private final EventPublisherProxy mockEventPublisherProxy = mock(EventPublisherProxy.class);
    private final EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
            mockEventPublisherProxy);

    private static final String PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME = "publishedEventCodeSet";

    @Before
    public void setupMocks() {

        // Mock DynamoDbProxy
        final Map<String, AttributeValue> initialItem = new HashMap<>();
        initialItem.put(PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME, AttributeValue.builder().ss("TEST1").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(initialItem);
        doNothing().when(mockDynamoDbProxy).updatePublishedEventCodeSet(anyInt(), anySet());

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
    public void test_handleRequest_successfully_publishes_play_by_play_events_for_a_game() {

        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(1));
        verify(mockEventPublisherProxy, times(1)).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updatePublishedEventCodeSet(anyInt(), anySet());
    }

    @Test
    public void test_handleRequest_does_not_publish_events_when_there_are_no_new_events() {

        final Map<String, AttributeValue> initialItem = new HashMap<>();
        initialItem.put(PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME, AttributeValue.builder().ss("TEST1", "TEST2").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(initialItem);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(0));
        verify(mockEventPublisherProxy, never()).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, never()).updatePublishedEventCodeSet(anyInt(), anySet());
    }

    @Test
    public void test_handleRequest_does_not_publish_events_when_there_are_there_are_events_previously_published_no_longer_in_the_play_by_play_response() {

        final Map<String, AttributeValue> initialItem = new HashMap<>();
        initialItem.put(PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME, AttributeValue.builder().ss("TEST1", "TEST2", "TESTX").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(initialItem);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(0));
        verify(mockEventPublisherProxy, never()).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, never()).updatePublishedEventCodeSet(anyInt(), anySet());
    }

    @Test
    public void test_handleRequest_does_not_publish_events_when_the_nhl_play_by_play_response_is_empty() {

        when(mockNhlPlayByPlayProxy.getPlayByPlayData(any(EventPublisherRequest.class))).thenReturn(Optional.empty());
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(0));
        verify(mockEventPublisherProxy, never()).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, never()).updatePublishedEventCodeSet(anyInt(), anySet());
    }

    @Test
    public void test_handleRequest_does_not_publish_events_when_the_nhl_play_by_play_response_has_null_current_play() {

        NhlLiveGameFeedResponse.getLiveData().getPlays().setCurrentPlay(null);
        when(mockNhlPlayByPlayProxy.getPlayByPlayData(any(EventPublisherRequest.class))).thenReturn(Optional.of(NhlLiveGameFeedResponse));
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, Integer> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final Integer responseNumberOfEventsPublished = result.get("numberOfEventsPublished");
        assertThat(responseNumberOfEventsPublished, is(notNullValue()));
        assertThat(responseNumberOfEventsPublished, is(0));
        verify(mockEventPublisherProxy, never()).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, never()).updatePublishedEventCodeSet(anyInt(), anySet());
    }
}
