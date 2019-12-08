package iansteph.nhlp3.eventpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.Teams;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.proxy.DynamoDbProxy;
import iansteph.nhlp3.eventpublisher.proxy.EventPublisherProxy;
import iansteph.nhlp3.eventpublisher.proxy.NhlPlayByPlayProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.DeleteRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.RemoveTargetsRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.sns.SnsClient;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Handler for requests to Lambda function.
 */
public class EventPublisherHandler implements RequestHandler<EventPublisherRequest, Object> {

    private final CloudWatchEventsClient cloudWatchEventsClient;
    private final DynamoDbProxy dynamoDbProxy;
    private final EventPublisherProxy eventPublisherProxy;
    private final NhlPlayByPlayProxy nhlPlayByPlayProxy;

    private static final String APP_CONFIG_FILE_NAME = "application-configuration.json";
    private static final Logger logger = LogManager.getLogger(EventPublisherHandler.class);

    public EventPublisherHandler() {
        final JsonNode appConfig = initializeAppConfig();

        // DynamoDB
        final DynamoDbClient dynamoDbClient = DynamoDbClient.create();
        this.dynamoDbProxy = new DynamoDbProxy(dynamoDbClient, appConfig.get("nhlPlayByPlayProcessingDynamoDbTableName").asText());

        // S3
        final AmazonS3 amazonS3Client = AmazonS3ClientBuilder.defaultClient();

        // NHL Play-By-Play
        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(createRestTemplateAndRegisterCustomObjectMapper());
        this.nhlPlayByPlayProxy = new NhlPlayByPlayProxy(nhlPlayByPlayClient, amazonS3Client,
                appConfig.get("nhlPlayByPlayResponseArchiveS3BucketName").asText());

        // SNS
        final SnsClient snsClient = SnsClient.create();
        this.eventPublisherProxy = new EventPublisherProxy(snsClient, new ObjectMapper(),
                appConfig.get("nhlPlayByPlayEventsTopicArn").asText());

        // CloudWatch
        this.cloudWatchEventsClient = CloudWatchEventsClient.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }

    public EventPublisherHandler(
            final DynamoDbProxy dynamoDbProxy,
            final NhlPlayByPlayProxy nhlPlayByPlayProxy,
            final EventPublisherProxy eventPublisherProxy,
            final CloudWatchEventsClient cloudWatchEventsClient
    ) {

        this.dynamoDbProxy = dynamoDbProxy;
        this.nhlPlayByPlayProxy = nhlPlayByPlayProxy;
        this.eventPublisherProxy = eventPublisherProxy;
        this.cloudWatchEventsClient = cloudWatchEventsClient;
    }

    public Map<String, AttributeValue> handleRequest(final EventPublisherRequest eventPublisherRequest, final Context context) {

        final Map<String, AttributeValue> nhlPlayByPlayProcessingItem =
                dynamoDbProxy.getNhlPlayByPlayProcessingItem(eventPublisherRequest);
        final String lastProcessedTimestamp = nhlPlayByPlayProcessingItem.get("lastProcessedTimeStamp").s();
        final Optional<NhlLiveGameFeedResponse> nhlLiveGameFeedResponse = nhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(
                lastProcessedTimestamp, eventPublisherRequest);
        if (nhlLiveGameFeedResponse.isPresent()) {

            final NhlLiveGameFeedResponse response = nhlLiveGameFeedResponse.get();
            final List<PlayEvent> playEvents = splitPlayByPlayResponseIntoPlaysSinceLastTimestamp(nhlPlayByPlayProcessingItem,
                    response);
            logger.info(format("%s event(s) since last event processed", playEvents.size()));
            final Teams teamsInPlay = response.getGameData().getTeams();
            playEvents.forEach(p -> eventPublisherProxy.publish(p, teamsInPlay.getHome().getId(), teamsInPlay.getAway().getId()));

            // If game is over delete the CloudWatch Event Rule that triggers the events for the game
            if (isGameCompleted(nhlLiveGameFeedResponse.get())) {

                deleteCloudWatchEventRulesForCompletedGame(response);
            }
        }
        return dynamoDbProxy.updateNhlPlayByPlayProcessingItem(nhlPlayByPlayProcessingItem, nhlLiveGameFeedResponse);
    }

    private JsonNode initializeAppConfig() {

        final String stage = System.getenv("Stage");
        final ObjectMapper objectMapper = new ObjectMapper();
        try {

            final String appConfigFilePath = this.getClass().getClassLoader().getResource(APP_CONFIG_FILE_NAME).getFile();
            final File appConfigFile = new File(appConfigFilePath);
            final JsonNode appConfig = objectMapper.readTree(appConfigFile);
            logger.info(format("Initialized app config for stage: %s", stage));
            return appConfig.get(stage);
        }
        catch (JsonProcessingException e) {

            logger.error(format("Encountered exception parsing JSON in app config file. Exception: %s", e.getMessage()), e);
            throw new RuntimeException(e);
        }
        catch (IOException e) {

            logger.error(format("Encountered exception reading app config file. Exception: %s", e.getMessage()), e);
            throw new RuntimeException(e);
        }
    }

    private RestTemplate createRestTemplateAndRegisterCustomObjectMapper() {

        final RestTemplate restTemplate = new RestTemplate();
        final MappingJackson2HttpMessageConverter messageConverter = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst().orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"));
        messageConverter.getObjectMapper().registerModule(new JavaTimeModule());
        return restTemplate;
    }

    private boolean isGameCompleted(final NhlLiveGameFeedResponse nhlLiveGameFeedResponse) {

        return nhlLiveGameFeedResponse.getGameData().getStatus().getAbstractGameState().toLowerCase()
                .equals("final");
    }

    private List<PlayEvent> splitPlayByPlayResponseIntoPlaysSinceLastTimestamp(
            final Map<String, AttributeValue> nhlPlayByPlayProcessingItem,
            final NhlLiveGameFeedResponse nhlLiveGameFeedResponse
    ) {

        final int lastProcessedEventIndex = Integer.parseInt(nhlPlayByPlayProcessingItem.get("lastProcessedEventIndex").n());

        // Add 1 because lastEventIndex was already processed (unless index is 0 then nothing has been processed yet)
        final int startPlayIndex = lastProcessedEventIndex == 0 ? lastProcessedEventIndex : lastProcessedEventIndex + 1;

        // Add 1 because the current play should be included
        final int currentPlayIndex = nhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().getAbout().getEventIdx();
        final int endPlayIndex = currentPlayIndex + 1;
        if (lastProcessedEventIndex == currentPlayIndex && currentPlayIndex != 0) {
            logger.info("No new events to publish");
            return Collections.emptyList();
        }
        else {

            final List<Play> playsToPublish = nhlLiveGameFeedResponse.getLiveData().getPlays().getAllPlays().subList(startPlayIndex,
                    endPlayIndex);
            return playsToPublish.stream()
                    .map(p -> new PlayEvent().withGamePk(nhlLiveGameFeedResponse.getGamePk()).withPlay(p))
                    .collect(Collectors.toList());
        }
    }

    private void deleteCloudWatchEventRulesForCompletedGame(final NhlLiveGameFeedResponse nhlLiveGameFeedResponse) {

        final boolean isGameCompleted = nhlLiveGameFeedResponse.getGameData().getStatus().getAbstractGameState().toLowerCase()
                .equals("final");
        if (isGameCompleted) {

            final int gamePk = nhlLiveGameFeedResponse.getGamePk();
            final String eventRuleName = String.format("GameId-%s", gamePk);
            final String eventBusName = "default";
            logger.info(format("Attempting to delete CloudWatch Event Rule %s , because the corresponding game has ended", eventRuleName));

            // Remove all Targets for CloudWatch Event Rule before it can be removed
            final RemoveTargetsRequest removeTargetsRequest = RemoveTargetsRequest.builder()
                    .rule(eventRuleName)
                    .eventBusName(eventBusName)
                    .ids("Event-Publisher-Lambda-Function")
                    .build();
            cloudWatchEventsClient.removeTargets(removeTargetsRequest);

            // Delete CloudWatch Event Rule to stop sending events for game that is finished
            final DeleteRuleRequest deleteRuleRequest = DeleteRuleRequest.builder()
                    .name(eventRuleName)
                    .eventBusName(eventBusName)
                    .build();
            cloudWatchEventsClient.deleteRule(deleteRuleRequest);
            logger.info(format("Successfully deleted CloudWatch Event Rule %s , because the corresponding game has ended", eventRuleName));
        }
    }
}
