package iansteph.nhlp3.eventpublisher.client;

import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.junit.Test;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NhlPlayByPlayClientTest extends UnitTestBase {

    private final RestTemplate mockRestTemplate = mock(RestTemplate.class);

    @Test
    public void test_getPlayByPlayData_successfully_retrieves_response_from_nhl_play_by_play_api() {

        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = NhlLiveGameFeedResponse;
        when(mockRestTemplate.getForObject(any(URI.class), any())).thenReturn(nhlLiveGameFeedResponse);
        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(mockRestTemplate);

        final NhlLiveGameFeedResponse response = nhlPlayByPlayClient.getPlayByPlayData(nhlLiveGameFeedResponse.getGamePk());

        assertThat(response, is(notNullValue()));
    }

    @Test(expected = RestClientException.class)
    public void test_getPlayByPlayData_throws_RestClientException_when_call_to_nhl_play_by_play_api_fails() {

        when(mockRestTemplate.getForObject(any(URI.class), any())).thenThrow(new RestClientException("Testing"));
        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(mockRestTemplate);

        nhlPlayByPlayClient.getPlayByPlayData(GameId);
    }
}
