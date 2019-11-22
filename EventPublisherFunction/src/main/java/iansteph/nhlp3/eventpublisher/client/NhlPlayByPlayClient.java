package iansteph.nhlp3.eventpublisher.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class NhlPlayByPlayClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LogManager.getLogger(NhlPlayByPlayClient.class);

    public NhlPlayByPlayClient(final RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        this.objectMapper = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst()
                .orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"))
                .getObjectMapper();
    }

    public String getPlayByPlayEventsSinceLastProcessedTimestamp(
            final int gameId,
            final String lastProcessedTimestamp
    ) {

        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("statsapi.web.nhl.com")
                .path("api/v1/game/{gameId}/feed/live/diffPatch")
                .queryParam("startTimecode", lastProcessedTimestamp)
                .buildAndExpand(gameId);
        logger.info(format("Calling NHL Play-by-Play timestamp diff API with lastProcessedTimestamp: %s", lastProcessedTimestamp));
        return restTemplate.getForObject(uriComponents.toUri(), String.class);
    }

    public Optional<NhlLiveGameFeedResponse> deserializeResponse(
            final String responseAsString,
            final int gameId,
            final String lastProcessedTimestamp
    ) {

        // If there are no news events since lastProcessedTimestamp "[]" is returned as response and cannot be deserialized into a
        // NhlLiveGameFeedResponse object. Start with a length check as a signal to fail fast instead of replacing whitespace on a 15k line
        // string. The expectation is that if the length is under 10 the response is "[]".
        try {

            checkNotNull(responseAsString);
            if (responseAsString.length() <= 10 && responseAsString.replaceAll("\\s", "").equalsIgnoreCase("[]")) {

                logger.info(format("GameId %s | No new events returned in the NHL Play-by-Play API response at lastProcessedTimestamp %s",
                        gameId, lastProcessedTimestamp));
                return Optional.empty();
            }
            else {

                final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = objectMapper.readValue(responseAsString,
                        NhlLiveGameFeedResponse.class);
                return Optional.of(nhlLiveGameFeedResponse);
            }
        }
        catch (NullPointerException e) {

            logDeserializationError(gameId, lastProcessedTimestamp, e);
            throw e;
        }
        catch (IOException e) {

            logDeserializationError(gameId, lastProcessedTimestamp, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void logDeserializationError(final int gameId, final String lastProcessedTimestamp, final Throwable throwable) {

        logger.error(format("GameId %s | Encountered exception when deserializing the NHL Play-by-Play API response at " +
                "lastProcessedTimestamp %s", gameId, lastProcessedTimestamp), throwable);
    }
}