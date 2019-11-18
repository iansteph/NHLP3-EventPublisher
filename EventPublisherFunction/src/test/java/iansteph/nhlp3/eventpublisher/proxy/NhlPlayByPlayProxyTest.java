package iansteph.nhlp3.eventpublisher.proxy;

import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.handler.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.junit.Before;
import org.junit.Test;

import java.time.format.DateTimeParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NhlPlayByPlayProxyTest extends UnitTestBase {

    private NhlPlayByPlayClient mockNhlPlayByPlayClient = mock(NhlPlayByPlayClient.class);
    private NhlPlayByPlayProxy proxy = new NhlPlayByPlayProxy(mockNhlPlayByPlayClient);

    @Before
    public void setupMocks() {
        when(mockNhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(anyInt(), anyString()))
                .thenReturn(new NhlLiveGameFeedResponse());
    }

    @Test
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampIsSuccessful() {
        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(999);

        final NhlLiveGameFeedResponse response = proxy.getPlayByPlayEventsSinceLastProcessedTimestamp("20191021_073000",
                eventPublisherRequest);

        assertThat(response, is(notNullValue()));
    }

    @Test(expected = NullPointerException.class)
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampThrowsNullPointerExceptionWhenEventPublisherRequestIsNull() {
        proxy.getPlayByPlayEventsSinceLastProcessedTimestamp("20191021_073000", null);
    }

    @Test(expected = NullPointerException.class)
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampThrowsNullPointerExceptionWhenLastProcessedTimestampIsNull() {
        proxy.getPlayByPlayEventsSinceLastProcessedTimestamp(null, EventPublisherRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampThrowsIllegalArgumentExceptionWhenLastProcessedTimestampIsNotCorrectLength() {
        proxy.getPlayByPlayEventsSinceLastProcessedTimestamp("badArgument", EventPublisherRequest);
    }

    @Test(expected = DateTimeParseException.class)
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampThrowsDateTimeParseExceptionWhenLastProcessedTimestampDateIsInvalid() {
        proxy.getPlayByPlayEventsSinceLastProcessedTimestamp("20191332_073000", EventPublisherRequest);
    }

    @Test(expected = DateTimeParseException.class)
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampThrowsNullPointerExceptionWhenLastProcessedTimestampTimeIsInvalid() {
        proxy.getPlayByPlayEventsSinceLastProcessedTimestamp("20191021_256060", EventPublisherRequest);
    }
}
