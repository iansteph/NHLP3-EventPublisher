package iansteph.nhlp3.eventpublisher.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.String.format;

public class NhlPlayByPlayClient {

    private RestTemplate restTemplate;

    private static final Logger logger = LogManager.getLogger(NhlPlayByPlayClient.class);

    public NhlPlayByPlayClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getPlayByPlayEventsSinceLastProcessedTimestamp(final int gameId, final String lastProcessedTimestamp) {
        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("statsapi.web.nhl.com")
                .path("api/v1/game/{gameId}/feed/live/diffPatch")
                .queryParam("startTimecode", lastProcessedTimestamp)
                .buildAndExpand(gameId);
        logger.info(format("Calling NHL Play-by-Play timestamp diff API with lastProcessedTimestamp: %s", lastProcessedTimestamp));
        final String response = restTemplate.getForObject(uriComponents.toUri(), String.class);
        logger.info(format("GameId %s | NHL Play-by-Play timestamp diff API response: %s", gameId, response));
        return response;
    }
}