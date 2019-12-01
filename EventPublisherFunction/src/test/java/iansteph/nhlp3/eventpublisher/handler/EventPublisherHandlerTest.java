package iansteph.nhlp3.eventpublisher.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.About;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.proxy.DynamoDbProxy;
import iansteph.nhlp3.eventpublisher.proxy.EventPublisherProxy;
import iansteph.nhlp3.eventpublisher.proxy.NhlPlayByPlayProxy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
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
    private final CloudWatchEventsClient mockCloudWatchEventsClient = mock(CloudWatchEventsClient.class);

    @Before
    public void setupMocks() {

        // Mock DynamoDBMapper
        final Map<String, AttributeValue> initialItem = new HashMap<>();
        initialItem.put("lastProcessedEventIndex", AttributeValue.builder().n("0").build());
        initialItem.put("lastProcessedTimeStamp", AttributeValue.builder().s("20191027_160744").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest.class)))
                .thenReturn(initialItem);
        final Map<String, AttributeValue> updatedItem = new HashMap<>();
        updatedItem.put("lastProcessedEventIndex", AttributeValue.builder().n("1").build());
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(), any())).thenReturn(updatedItem);

        // Mock NhlPlayByPlayProxy
        when(mockNhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(anyString(), any(EventPublisherRequest.class)))
                .thenReturn(Optional.of(NhlLiveGameFeedResponse));

        // Mock EventPublisherProxy
        when(mockEventPublisherProxy.publish(any(PlayEvent.class), anyInt(), anyInt())).thenReturn(PublishResponse.builder().build());
    }

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void testConstructorSuccessfullyInitializesEventPublisherHandler() {

        environmentVariables.set("Stage", "personal");

        final EventPublisherHandler eventPublisherHandler = new EventPublisherHandler();

        assertThat(eventPublisherHandler, is(notNullValue()));
    }

    @Test
    public void testHandleRequestIsSuccessful() {

        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy, mockCloudWatchEventsClient);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, AttributeValue> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final AttributeValue responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(Integer.parseInt(responseLastProcessedEventIndex.n()), is(1));
        verify(mockEventPublisherProxy, atLeast(1)).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updateNhlPlayByPlayProcessingItem(any(), any());
    }

    @Test
    public void testHandleRequestPublishesFirstPlayByPlayEvent() {

        final About about = new About();
        about.setEventIdx(0);
        NhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().setAbout(about);
        when(mockNhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(anyString(), any(EventPublisherRequest.class)))
                .thenReturn(Optional.of(NhlLiveGameFeedResponse));
        final Map<String, AttributeValue> updatedItem = new HashMap<>();
        updatedItem.put("lastProcessedEventIndex", AttributeValue.builder().n("0").build());
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(), any())).thenReturn(updatedItem);
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy, mockCloudWatchEventsClient);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, AttributeValue> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final AttributeValue responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(Integer.parseInt(responseLastProcessedEventIndex.n()), is(0));
        verify(mockEventPublisherProxy, times(1)).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updateNhlPlayByPlayProcessingItem(any(), any());
    }

    @Test
    public void testHandleRequestDoesNotPublishAnyEventsIfThereIsNoNewEventSinceLastEventProcessed() {

        final About about = new About();
        about.setEventIdx(1);
        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = createNhlLiveGameFeedResponse();
        nhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().setAbout(about);
        when(mockNhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(anyString(), any(EventPublisherRequest.class))).thenReturn(Optional.of(nhlLiveGameFeedResponse));
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("lastProcessedEventIndex", AttributeValue.builder().n("1").build());
        item.put("lastProcessedTimeStamp", AttributeValue.builder().s("20191027_160744").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(item);
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(), any())).thenReturn(item);
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy, mockCloudWatchEventsClient);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, AttributeValue> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final AttributeValue responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(Integer.parseInt(responseLastProcessedEventIndex.n()), is(1));
        verify(mockEventPublisherProxy, times(0)).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updateNhlPlayByPlayProcessingItem(any(), any());
    }

    @Test
    public void testHandleRequestDoesNotPublishAnyEventsIfNhlPlayByPlayResponseIsEmptyArray() {

        when(mockNhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(anyString(), any(EventPublisherRequest.class)))
                .thenReturn(Optional.empty());
        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("lastProcessedEventIndex", AttributeValue.builder().n("0").build());
        item.put("lastProcessedTimeStamp", AttributeValue.builder().s("20191027_160744").build());
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(item);
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy, mockCloudWatchEventsClient);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Map<String, AttributeValue> result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(not(Collections.emptyMap())));
        final AttributeValue responseLastProcessedEventIndex = result.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(Integer.parseInt(responseLastProcessedEventIndex.n()), is(1));
        verify(mockEventPublisherProxy, times(0)).publish(any(PlayEvent.class), anyInt(), anyInt());
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class));
        verify(mockDynamoDbProxy, times(1)).updateNhlPlayByPlayProcessingItem(any(), any());
    }
}
