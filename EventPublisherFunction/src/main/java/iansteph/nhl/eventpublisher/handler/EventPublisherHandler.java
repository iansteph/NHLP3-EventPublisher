package iansteph.nhl.eventpublisher.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhl.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhl.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;
import iansteph.nhl.eventpublisher.model.event.PlayEvent;
import iansteph.nhl.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhl.eventpublisher.model.nhl.gamedata.Teams;
import iansteph.nhl.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhl.eventpublisher.proxy.DynamoDbProxy;
import iansteph.nhl.eventpublisher.proxy.EventPublisherProxy;
import iansteph.nhl.eventpublisher.proxy.NhlPlayByPlayProxy;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.cloudwatchevents.CloudWatchEventsClient;
import software.amazon.awssdk.services.cloudwatchevents.model.DeleteRuleRequest;
import software.amazon.awssdk.services.cloudwatchevents.model.RemoveTargetsRequest;

import java.util.Collections;
import java.util.List;
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


    public EventPublisherHandler() {
        final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().build();
        final DynamoDBMapper dynamoDbMapper = new DynamoDBMapper(amazonDynamoDB);
        this.dynamoDbProxy = new DynamoDbProxy(dynamoDbMapper);

        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(new RestTemplate());
        this.nhlPlayByPlayProxy = new NhlPlayByPlayProxy(nhlPlayByPlayClient);

        this.eventPublisherProxy = new EventPublisherProxy(AmazonSNSClientBuilder.defaultClient(), new ObjectMapper());

        this.cloudWatchEventsClient = CloudWatchEventsClient.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }

    public EventPublisherHandler(final DynamoDbProxy dynamoDbProxy, final NhlPlayByPlayProxy nhlPlayByPlayProxy,
            final EventPublisherProxy eventPublisherProxy, final CloudWatchEventsClient cloudWatchEventsClient) {
        this.dynamoDbProxy = dynamoDbProxy;
        this.nhlPlayByPlayProxy = nhlPlayByPlayProxy;
        this.eventPublisherProxy = eventPublisherProxy;
        this.cloudWatchEventsClient = cloudWatchEventsClient;
    }

    public NhlPlayByPlayProcessingItem handleRequest(final EventPublisherRequest eventPublisherRequest, final Context context) {
        final NhlPlayByPlayProcessingItem nhlPlayByPlayProcessingItem =
                dynamoDbProxy.getNhlPlayByPlayProcessingItem(eventPublisherRequest);
        System.out.println(format("Retrieving NhlPlayByPlayProcessingItem from DynamoDB: %s", nhlPlayByPlayProcessingItem));
        final String lastProcessedTimestamp = nhlPlayByPlayProcessingItem.getLastProcessedTimeStamp();

        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = nhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(
                lastProcessedTimestamp, eventPublisherRequest);
        System.out.println(format("Calling NHL Play-by-Play timestamp diff API: %s", nhlLiveGameFeedResponse));

        final List<PlayEvent> playEvents = splitPlayByPlayResponseIntoPlaysSinceLastTimestamp(nhlPlayByPlayProcessingItem,
                nhlLiveGameFeedResponse);
        System.out.println(format("%s event(s) since last event processed. Events to process: %s", playEvents.size(), playEvents));
        final Teams teamsInPlay = nhlLiveGameFeedResponse.getGameData().getTeams();
        playEvents.forEach(p -> eventPublisherProxy.publish(p, teamsInPlay.getHome().getId(), teamsInPlay.getAway().getId()));

        final NhlPlayByPlayProcessingItem updatedItem = dynamoDbProxy.updateNhlPlayByPlayProcessingItem(nhlPlayByPlayProcessingItem,
                nhlLiveGameFeedResponse);
        System.out.println(format("Saved updated NhlPlayByPlayProcessingItem to DyanmoDB: %s", updatedItem));

        deleteCloudWatchEventRulesForCompletedGame(nhlLiveGameFeedResponse);

        return updatedItem;
    }

    private List<PlayEvent> splitPlayByPlayResponseIntoPlaysSinceLastTimestamp(final NhlPlayByPlayProcessingItem nhlPlayByPlayProcessingItem,
            final NhlLiveGameFeedResponse nhlLiveGameFeedResponse) {
        final int lastProcessedEventIndex = nhlPlayByPlayProcessingItem.getLastProcessedEventIndex();

        // Add 1 because lastEventIndex was already processed
        final int startPlayIndex = lastProcessedEventIndex + 1;

        // Add 1 because the current play should be included
        final int currentPlayIndex = nhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().getAbout().getEventIdx();
        final int endPlayIndex = currentPlayIndex + 1;
        if (lastProcessedEventIndex == currentPlayIndex) {
            return Collections.emptyList(); // There is nothing to publish, because there are no new events
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
            System.out.println(format("Successfully deleted CloudWatch Event Rule %s , because the corresponding game has ended",
                    eventRuleName));
        }
    }
}
