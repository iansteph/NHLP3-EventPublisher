package iansteph.nhlp3.eventpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import iansteph.nhlp3.eventpublisher.AppConfig;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.Teams;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.proxy.DynamoDbProxy;
import iansteph.nhlp3.eventpublisher.proxy.EventPublisherProxy;
import iansteph.nhlp3.eventpublisher.proxy.NhlPlayByPlayProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.utils.AttributeMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Handler for requests to Lambda function.
 */
public class EventPublisherHandler implements RequestHandler<EventPublisherRequest, Object> {

    private final DynamoDbProxy dynamoDbProxy;
    private final EventPublisherProxy eventPublisherProxy;
    private final NhlPlayByPlayProxy nhlPlayByPlayProxy;

    private static final Logger logger = LogManager.getLogger(EventPublisherHandler.class);
    private static final String PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME = "publishedEventCodeSet";

    public EventPublisherHandler() {

        final JsonNode appConfig = AppConfig.initialize();

        // Common
        final AwsCredentialsProvider defaultAwsCredentialsProvider = DefaultCredentialsProvider.builder().build();
        final SdkHttpClient httpClient = ApacheHttpClient.builder().buildWithDefaults(AttributeMap.empty());

        // DynamoDB
        final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(defaultAwsCredentialsProvider)
                .endpointOverride(URI.create("https://dynamodb.us-east-1.amazonaws.com/"))
                .httpClient(httpClient)
                .region(Region.US_EAST_1)
                .build();
        this.dynamoDbProxy = new DynamoDbProxy(dynamoDbClient, appConfig.get("nhlPlayByPlayProcessingDynamoDbTableName").asText());

        // S3
        final S3Client s3Client = S3Client.builder()
                .credentialsProvider(defaultAwsCredentialsProvider)
                .endpointOverride(URI.create("https://s3.us-east-1.amazonaws.com/"))
                .httpClient(httpClient)
                .region(Region.US_EAST_1)
                .build();

        // NHL Play-By-Play
        final RestTemplate restTemplate = createRestTemplateAndRegisterCustomObjectMapper();
        final ObjectMapper restTemplateObjectMapper = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst().orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"))
                .getObjectMapper();
        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(restTemplate);
        this.nhlPlayByPlayProxy = new NhlPlayByPlayProxy(nhlPlayByPlayClient, restTemplateObjectMapper, s3Client,
                appConfig.get("nhlPlayByPlayResponseArchiveS3BucketName").asText());

        // SNS
        final SnsClient snsClient = SnsClient.builder()
                .credentialsProvider(defaultAwsCredentialsProvider)
                .endpointOverride(URI.create("https://sns.us-east-1.amazonaws.com/"))
                .httpClient(httpClient)
                .region(Region.US_EAST_1)
                .build();
        this.eventPublisherProxy = new EventPublisherProxy(snsClient, new ObjectMapper(),
                appConfig.get("nhlPlayByPlayEventsTopicArn").asText());
    }

    public EventPublisherHandler(
            final DynamoDbProxy dynamoDbProxy,
            final NhlPlayByPlayProxy nhlPlayByPlayProxy,
            final EventPublisherProxy eventPublisherProxy
    ) {

        this.dynamoDbProxy = dynamoDbProxy;
        this.nhlPlayByPlayProxy = nhlPlayByPlayProxy;
        this.eventPublisherProxy = eventPublisherProxy;
    }

    public Map<String, Integer> handleRequest(final EventPublisherRequest eventPublisherRequest, final Context context) {

        final Map<String, AttributeValue> nhlPlayByPlayProcessingItem =
                dynamoDbProxy.getNhlPlayByPlayProcessingItem(eventPublisherRequest);
        final Optional<NhlLiveGameFeedResponse> nhlLiveGameFeedResponse = nhlPlayByPlayProxy.getPlayByPlayData(eventPublisherRequest);
        final Map<String, Integer> responseMap = new HashMap<>();
        responseMap.put("numberOfEventsPublished", 0);
        if (validateNhlPlayByPlayResponseIsPopulated(nhlLiveGameFeedResponse)) {

            final NhlLiveGameFeedResponse response = nhlLiveGameFeedResponse.get();
            final Map<String, Integer> playByPlayResponseEventCodeToIndexMap = buildEventCodeToIndexMap(response);
            final Set<String> publishedEventCodes = getPublishedEventCodes(nhlPlayByPlayProcessingItem);
            final Map<String, Integer> eventIndicesToPublish = calculateEventIndicesToPublish(playByPlayResponseEventCodeToIndexMap,
                    publishedEventCodes);
            if (eventIndicesToPublish.size() > 0) {

                final List<PlayEvent> playEventsToPublish = retrievePlayEventsToPublishFromPlayByPlayResponse(response, eventIndicesToPublish);
                publishPlays(response, playEventsToPublish);
                updatePublishedEventCodeSet(response.getGamePk(), eventIndicesToPublish.keySet());
            }
            responseMap.put("numberOfEventsPublished", eventIndicesToPublish.size());
        }
        return responseMap;
    }

    private boolean validateNhlPlayByPlayResponseIsPopulated(final Optional<NhlLiveGameFeedResponse> nhlLiveGameFeedResponse) {

        return nhlLiveGameFeedResponse.isPresent() && nhlLiveGameFeedResponse.get().getLiveData().getPlays().getCurrentPlay() != null;
    }

    private RestTemplate createRestTemplateAndRegisterCustomObjectMapper() {

        final RestTemplate restTemplate = new RestTemplate();
        final MappingJackson2HttpMessageConverter messageConverter = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst().orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"));
        messageConverter.getObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                false);
        return restTemplate;
    }

    private Map<String, Integer> buildEventCodeToIndexMap(final NhlLiveGameFeedResponse nhlLiveGameFeedResponse) {

        final Map<String, Integer> eventCodeToIndexMap = new HashMap<>();
        nhlLiveGameFeedResponse.getLiveData().getPlays().getAllPlays().forEach(play -> {
            final String eventCode = play.getResult().getEventCode();
            final Integer index = play.getAbout().getEventIdx();
            eventCodeToIndexMap.put(eventCode, index);
        });
        return eventCodeToIndexMap;
    }

    private Set<String> getPublishedEventCodes(final Map<String, AttributeValue> nhlPlayByPlayProcessingItem) {

        final Set<String> publishedEventCodeSet =
                nhlPlayByPlayProcessingItem.containsKey(PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME) ?
                        new HashSet<>(nhlPlayByPlayProcessingItem.get(PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME).ss()) :
                        new HashSet<>();
        logger.info(format("%s eventCodes have been published so far: %s",publishedEventCodeSet.size(), publishedEventCodeSet));
        return publishedEventCodeSet;
    }

    private Map<String, Integer> calculateEventIndicesToPublish(
            final Map<String, Integer> masterEventCodeToIndexMap,
            final Set<String> publishedEventCodeSet
    ) {

        final Set<String> eventCodesToPublish = new HashSet<>(masterEventCodeToIndexMap.keySet());
        eventCodesToPublish.removeAll(publishedEventCodeSet);
        final Map<String, Integer> eventCodeToIndexMapToPublish = new HashMap<>();
        eventCodesToPublish.forEach(eventCode -> eventCodeToIndexMapToPublish.put(eventCode, masterEventCodeToIndexMap.get(eventCode)));
        logger.info(format("%s eventCode(s) to be published: %s", eventCodeToIndexMapToPublish.size(), eventCodeToIndexMapToPublish));
        return eventCodeToIndexMapToPublish;
    }

    private List<PlayEvent> retrievePlayEventsToPublishFromPlayByPlayResponse(
            final NhlLiveGameFeedResponse nhlLiveGameFeedResponse,
            final Map<String, Integer> eventCodeToEventIndexMapToPublish
    ) {

        final List<Integer> sortedEventIndicesToPublish = new ArrayList<>(eventCodeToEventIndexMapToPublish.values());
        Collections.sort(sortedEventIndicesToPublish);
        final int gamePk = nhlLiveGameFeedResponse.getGamePk();
        final List<PlayEvent> playEventsToPublish = sortedEventIndicesToPublish.stream()
                .map(eventIndex ->
                        new PlayEvent()
                                .withGamePk(gamePk)
                                .withPlay(nhlLiveGameFeedResponse.getLiveData().getPlays().getAllPlays().get(eventIndex)))
                .collect(Collectors.toList());
        logger.info(format("Sorted order of PlayEvents to be published: %s", playEventsToPublish));
        return playEventsToPublish;
    }

    private void publishPlays(
            final NhlLiveGameFeedResponse nhlLiveGameFeedResponse,
            final List<PlayEvent> playEventsToPublish
    ) {

        final Teams teamsInPlay = nhlLiveGameFeedResponse.getGameData().getTeams();
        final int homeTeamId = teamsInPlay.getHome().getId();
        final int awayTeamId = teamsInPlay.getAway().getId();
        logger.info("Publishing play events...");
        playEventsToPublish.forEach(playEvent -> eventPublisherProxy.publish(playEvent, homeTeamId, awayTeamId));
        logger.info("Play events published");
    }

    private void updatePublishedEventCodeSet(
            final int gamePk,
            final Set<String> publishedEventCodesToAdd
    ) {

        dynamoDbProxy.updatePublishedEventCodeSet(gamePk, publishedEventCodesToAdd);
        logger.info(format("Added the following event codes to the published event code set in the DynamoDB: %s",
                publishedEventCodesToAdd));
    }
}
