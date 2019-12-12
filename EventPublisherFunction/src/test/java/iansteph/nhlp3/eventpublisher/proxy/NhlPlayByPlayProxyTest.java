package iansteph.nhlp3.eventpublisher.proxy;

import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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
    private final S3Client mockS3Client = mock(S3Client.class);
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
        verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
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

    @Test
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampThrowsSdkExceptionWhenS3CallFailsWhenArchivingRequestsToS3() {

        when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(SdkException.create(null, null));

        proxy.getPlayByPlayEventsSinceLastProcessedTimestamp("20191021_073000", EventPublisherRequest);

        verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
