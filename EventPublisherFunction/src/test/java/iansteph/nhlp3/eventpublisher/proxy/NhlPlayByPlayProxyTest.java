package iansteph.nhlp3.eventpublisher.proxy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NhlPlayByPlayProxyTest extends UnitTestBase {

    private final NhlPlayByPlayClient mockNhlPlayByPlayClient = mock(NhlPlayByPlayClient.class);
    private final SafeObjectMapper mockObjectMapper = mock(SafeObjectMapper.class);
    private final S3Client mockS3Client = mock(S3Client.class);
    private final NhlPlayByPlayProxy proxy = new NhlPlayByPlayProxy(mockNhlPlayByPlayClient, mockObjectMapper, mockS3Client,
            "someBucketName");

    @Before
    public void setupMocks() throws IOException {

        when(mockNhlPlayByPlayClient.getPlayByPlayData(anyInt())).thenReturn(getPlayByPlayResponseFromTestResource());
        when(mockObjectMapper.writeValueAsString(any(NhlLiveGameFeedResponse.class))).thenReturn("someSerializedString");
    }

    @Test
    public void test_getPlayByPlayData_is_successful() {

        final EventPublisherRequest eventPublisherRequest = new EventPublisherRequest();
        eventPublisherRequest.setGameId(GameId);

        final Optional<NhlLiveGameFeedResponse> response = proxy.getPlayByPlayData(eventPublisherRequest);

        assertThat(response, is(notNullValue()));
        verify(mockNhlPlayByPlayClient, times(1)).getPlayByPlayData(anyInt());
        verify(mockObjectMapper, times(1)).writeValueAsString(any(NhlLiveGameFeedResponse.class));
        verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test(expected = NullPointerException.class)
    public void test_getPlayByPlayData_throws_NullPointerException_when_EventPublisherRequest_is_null() {

        proxy.getPlayByPlayData(null);

        verify(mockNhlPlayByPlayClient, never()).getPlayByPlayData(anyInt());
        verify(mockObjectMapper, never()).writeValueAsString(any(NhlLiveGameFeedResponse.class));
        verify(mockS3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    public void test_getPlayByPlayData_throws_JsonProcessingException_when_archive_request_to_S3_fails_on_serializing_nhl_play_by_play_api_response() throws JsonProcessingException {

        final ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        final NhlPlayByPlayProxy nhlPlayByPlayProxy = new NhlPlayByPlayProxy(mockNhlPlayByPlayClient, mockObjectMapper, mockS3Client, "");
        when(mockObjectMapper.writeValueAsString(any(NhlLiveGameFeedResponse.class))).thenThrow(new JsonParseException(null, ""));

        nhlPlayByPlayProxy.getPlayByPlayData(EventPublisherRequest);

        verify(mockNhlPlayByPlayClient, times(1)).getPlayByPlayData(anyInt());
        verify(mockObjectMapper, times(1)).writeValueAsString(any(NhlLiveGameFeedResponse.class));
        verify(mockS3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    public void test_getPlayByPlayData_throws_SdkException_when_archive_request_to_S3_fails_on_sending_request_to_S3() {

        when(mockS3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenThrow(SdkException.create(null, null));

        proxy.getPlayByPlayData(EventPublisherRequest);

        verify(mockNhlPlayByPlayClient, times(1)).getPlayByPlayData(anyInt());
        verify(mockObjectMapper, times(1)).writeValueAsString(any(NhlLiveGameFeedResponse.class));
        verify(mockS3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
