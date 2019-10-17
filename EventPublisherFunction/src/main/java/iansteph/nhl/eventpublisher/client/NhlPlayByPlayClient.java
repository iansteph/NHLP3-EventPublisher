package iansteph.nhl.eventpublisher.client;

import iansteph.nhl.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class NhlPlayByPlayClient {

    private RestTemplate restTemplate;

    public NhlPlayByPlayClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NhlLiveGameFeedResponse getPlayByPlayEventsSinceLastProcessedTimestamp(final int gameId, final String lastProcessedTimestamp) {
        final UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("statsapi.web.nhl.com")
                .path("game/{gameId}/feed/live/diffPatch")
                .queryParam("startTimecode", lastProcessedTimestamp)
                .buildAndExpand(gameId);
        return restTemplate.getForObject(uriComponents.toUri(), NhlLiveGameFeedResponse.class);
    }
}