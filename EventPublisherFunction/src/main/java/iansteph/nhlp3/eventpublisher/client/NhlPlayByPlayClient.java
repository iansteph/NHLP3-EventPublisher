package iansteph.nhlp3.eventpublisher.client;

import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class NhlPlayByPlayClient {

    private final RestTemplate restTemplate;

    private static final Logger logger = LogManager.getLogger(NhlPlayByPlayClient.class);

    public NhlPlayByPlayClient(final RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    public NhlLiveGameFeedResponse getPlayByPlayData(final int gameId) {

        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("statsapi.web.nhl.com")
                .path("api/v1/game/{gameId}/feed/live/")
                .buildAndExpand(gameId);
        logger.info("Calling NHL Play-by-Play API");
        return restTemplate.getForObject(uriComponents.toUri(), NhlLiveGameFeedResponse.class);
    }

}