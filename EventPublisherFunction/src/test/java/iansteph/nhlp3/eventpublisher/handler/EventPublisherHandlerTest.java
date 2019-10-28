package iansteph.nhlp3.eventpublisher.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.sns.model.PublishResult;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.About;
import iansteph.nhlp3.eventpublisher.proxy.DynamoDbProxy;
import iansteph.nhlp3.eventpublisher.proxy.EventPublisherProxy;
import iansteph.nhlp3.eventpublisher.proxy.NhlPlayByPlayProxy;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;

public class EventPublisherHandlerTest extends UnitTestBase {

    private final DynamoDbProxy mockDynamoDbProxy = mock(DynamoDbProxy.class);
    private final NhlPlayByPlayProxy mockNhlPlayByPlayProxy = mock(NhlPlayByPlayProxy.class);
    private final EventPublisherProxy mockEventPublisherProxy = mock(EventPublisherProxy.class);
    private final CloudWatchEventsClient mockCloudWatchEventsClient = mock(CloudWatchEventsClient.class);



    @Before
    public void setupMocks() {
        // Mock DynamoDBMapper
        final NhlPlayByPlayProcessingItem initialItem = new NhlPlayByPlayProcessingItem();
        initialItem.setLastProcessedEventIndex(0);
        initialItem.setLastProcessedTimeStamp("20191027_160744");
        when(mockDynamoDbProxy.getNhlPlayByPlayProcessingItem(any(EventPublisherRequest.class))).thenReturn(initialItem);
        final NhlPlayByPlayProcessingItem updatedItem = new NhlPlayByPlayProcessingItem();
        updatedItem.setLastProcessedEventIndex(1);
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(NhlPlayByPlayProcessingItem.class),
                any(NhlLiveGameFeedResponse.class))).thenReturn(updatedItem);

        // Mock NhlPlayByPlayProxy
        when(mockNhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(anyString(), any(EventPublisherRequest.class)))
                .thenReturn(NhlLiveGameFeedResponse);

        // Mock EventPublisherProxy
        when(mockEventPublisherProxy.publish(any(PlayEvent.class), anyInt(), anyInt())).thenReturn(new PublishResult());
    }

    @Test
    public void testHandleRequestIsSuccessful() {
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy, mockCloudWatchEventsClient);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(999);

        final NhlPlayByPlayProcessingItem result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertNotNull(result);
        assertThat(result.getLastProcessedEventIndex(), is(1));
    }

    @Test
    public void testHandleRequestDoesNotPublishAnyEventsIfThereIsNoNewEventSinceLastEventProcessed() {
        final About about = new About();
        about.setEventIdx(0);
        NhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().setAbout(about);
        when(mockNhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(anyString(), any(EventPublisherRequest.class)))
                .thenReturn(NhlLiveGameFeedResponse);

        final NhlPlayByPlayProcessingItem updatedItem = new NhlPlayByPlayProcessingItem();
        updatedItem.setLastProcessedEventIndex(0);
        when(mockDynamoDbProxy.updateNhlPlayByPlayProcessingItem(any(NhlPlayByPlayProcessingItem.class),
                any(NhlLiveGameFeedResponse.class))).thenReturn(updatedItem);
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler(mockDynamoDbProxy, mockNhlPlayByPlayProxy,
                mockEventPublisherProxy, mockCloudWatchEventsClient);
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(999);

        final NhlPlayByPlayProcessingItem result = eventPublisherHandler.handleRequest(eventPublisherRequest, null);

        assertNotNull(result);
        assertThat(result.getLastProcessedEventIndex(), is(0));
        verify(mockEventPublisherProxy, times(0)).publish(any(PlayEvent.class), anyInt(), anyInt());
    }
}
