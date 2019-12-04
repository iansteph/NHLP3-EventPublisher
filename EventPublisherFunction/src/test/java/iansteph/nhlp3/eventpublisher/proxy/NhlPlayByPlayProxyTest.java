package iansteph.nhlp3.eventpublisher.proxy;

import com.amazonaws.services.s3.AmazonS3;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NhlPlayByPlayProxyTest extends UnitTestBase {

    private final NhlPlayByPlayClient mockNhlPlayByPlayClient = mock(NhlPlayByPlayClient.class);
    private final AmazonS3 mockS3Client = mock(AmazonS3.class);
    private final NhlPlayByPlayProxy proxy = new NhlPlayByPlayProxy(mockNhlPlayByPlayClient, mockS3Client, "someBucketName");

    @Before
    public void setupMocks() throws IOException {

        when(mockNhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(anyInt(), anyString()))
                .thenReturn(getTestPlayByPlayResponseResourceAsString());
    }

    @Test
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampIsSuccessful() {

        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Optional<NhlLiveGameFeedResponse> response = proxy.getPlayByPlayEventsSinceLastProcessedTimestamp("20191021_073000",
                eventPublisherRequest);

        verify(mockNhlPlayByPlayClient, times(1)).getPlayByPlayEventsSinceLastProcessedTimestamp(anyInt(),
                anyString());
        verify(mockS3Client, times(1)).putObject(anyString(), anyString(), anyString());
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
