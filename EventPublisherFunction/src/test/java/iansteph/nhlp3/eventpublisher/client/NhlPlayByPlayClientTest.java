package iansteph.nhlp3.eventpublisher.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NhlPlayByPlayClientTest extends UnitTestBase {

    private final RestTemplate mockRestTemplate = mock(RestTemplate.class);

    @Before
    public void setupMocks() throws IOException {

        final ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        when(mockObjectMapper.readValue(any(String.class), any(Class.class))).thenReturn(NhlLiveGameFeedResponse);
        when(mockObjectMapper.registerModule(any())).thenReturn(mockObjectMapper);
        final MappingJackson2HttpMessageConverter mockMappingJackson2HttpMessageConverter = mock(MappingJackson2HttpMessageConverter.class);
        when(mockMappingJackson2HttpMessageConverter.getObjectMapper()).thenReturn(mockObjectMapper);
        when(mockRestTemplate.getMessageConverters()).thenReturn(Collections.singletonList(mockMappingJackson2HttpMessageConverter));
    }

    @Test
    public void testConstructorSuccessfullyCreatesInstance() {

        final RestTemplate restTemplate = new RestTemplate();
        final MappingJackson2HttpMessageConverter messageConverter = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst().orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"));
        messageConverter.getObjectMapper().registerModule(new JavaTimeModule());

        final NhlPlayByPlayClient client = new NhlPlayByPlayClient(restTemplate);

        assertThat(client, is(notNullValue()));
    }

    @Test(expected = RuntimeException.class)
    public void testConstructorThrowsRuntimeExceptionIfMappingJackson2HttpMessageConverterCannotBeFoundInOrderToRetrieveObjectMapper() {

        final RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(new ArrayList<>());

        new NhlPlayByPlayClient(restTemplate);
    }

    @Test
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampSuccessfullyRetrievesNhlPlayByPlayResponse() {

        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = NhlLiveGameFeedResponse;
        when(mockRestTemplate.getForObject(any(URI.class), any())).thenReturn(nhlLiveGameFeedResponse.toString());
        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(mockRestTemplate);

        final String responseString = nhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(
                nhlLiveGameFeedResponse.getGamePk(), "20191118_000941");

        assertThat(responseString, is(notNullValue()));
    }

    @Test(expected = RestClientException.class)
    public void testGetPlayByPlayEventsSinceLastProcessedTimestampThrowsRestClientExceptionIfCallFails() {

        when(mockRestTemplate.getForObject(any(URI.class), any())).thenThrow(new RestClientException("Testing"));
        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(mockRestTemplate);

        nhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(GameId, "20191118_000941");
    }

    @Test
    public void testDeserializeResponseSuccessfullyDeserializesFullResponse() throws IOException {

        final String mockResponse = getTestPlayByPlayResponseResourceAsString();
        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(createRestTemplateForDeserializationUnitTest());

        final Optional<NhlLiveGameFeedResponse> response = nhlPlayByPlayClient.deserializeResponse(mockResponse, GameId, "20191118_000941");

        assertThat(response, is(notNullValue()));
        assertThat(response, is(not(Optional.empty())));
        assertThat(response.get().getGamePk(), is(GameId));
    }

    @Test
    public void testDeserializeResponseSuccessfullyDeserializesEmptyArrayResponse() {

        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(createRestTemplateForDeserializationUnitTest());

        final Optional<NhlLiveGameFeedResponse> response = nhlPlayByPlayClient.deserializeResponse("  [   ]  ", GameId, "20191118_000941");

        assertThat(response, is(notNullValue()));
        assertThat(response, is(Optional.empty()));

    }

    @Test(expected = NullPointerException.class)
    public void testDeserializeResponseThrowsNullPointerExceptionIfDeserializationStringIsNull() {

        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(createRestTemplateForDeserializationUnitTest());

        nhlPlayByPlayClient.deserializeResponse(null, GameId, "20191118_000941");
    }

    @Test(expected = RuntimeException.class)
    public void testDeserializeResponseThrowsRuntimeExceptionWrappedIOExceptionIfDeserializationFails() {

        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(createRestTemplateForDeserializationUnitTest());

        nhlPlayByPlayClient.deserializeResponse("This will fail deserialization", GameId, "20191118_000941");
    }

    private RestTemplate createRestTemplateForDeserializationUnitTest() {

        final RestTemplate restTemplate = new RestTemplate();
        final MappingJackson2HttpMessageConverter messageConverter = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst().orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"));
        messageConverter.getObjectMapper().registerModule(new JavaTimeModule());
        return restTemplate;
    }
}
