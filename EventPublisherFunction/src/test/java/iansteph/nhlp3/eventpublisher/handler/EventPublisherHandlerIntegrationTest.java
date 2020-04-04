package iansteph.nhlp3.eventpublisher.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import iansteph.nhlp3.eventpublisher.AppConfig;
import iansteph.nhlp3.eventpublisher.IntegrationTestBase;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicRequest;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.Subscription;
import software.amazon.awssdk.services.sns.model.UnsubscribeRequest;
import software.amazon.awssdk.services.sns.model.UnsubscribeResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;
import software.amazon.awssdk.services.sqs.model.PurgeQueueResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class EventPublisherHandlerIntegrationTest extends IntegrationTestBase {

    private static final LambdaClient LAMBDA_CLIENT = LambdaClient.create();
    private static final RestTemplate REST_TEMPLATE = createRestTemplateAndRegisterCustomObjectMapper();
    private static final ObjectMapper OBJECT_MAPPER = getObjectMapperFromRestTemplate(REST_TEMPLATE);
    private static final NhlPlayByPlayClient NHL_CLIENT = new NhlPlayByPlayClient(REST_TEMPLATE);
    private static final DynamoDbClient DYNAMO_DB_CLIENT = DynamoDbClient.create();
    private static final SnsClient SNS_CLIENT = SnsClient.create();
    private static final SqsClient SQS_CLIENT = SqsClient.create();

    private static final JsonNode APP_CONFIG = AppConfig.initialize(getStage());
    private static final String DYNAMO_DB_TABLE_NAME = APP_CONFIG.get("nhlPlayByPlayProcessingDynamoDbTableName").asText();
    private static final String LAMBDA_FUNCTION_NAME = APP_CONFIG.get("integration-tests").get("eventPublisherLambdaFunctionName").asText();
    private static final String QUEUE_NAME = APP_CONFIG.get("integration-tests").get("integrationTestingSqsQueueName").asText();
    private static final String TOPIC_ARN = APP_CONFIG.get("nhlPlayByPlayEventsTopicArn").asText();

    // Game delayed due to Jay Bouwmeester's cardiac episode: https://www.nhl.com/gamecenter/stl-vs-ana/2020/03/11/2019020876
    private static final String COMPOSITE_GAME_ID = "f8d32faab967c2f56a3c36a2be29c866~2019020876";
    private static final String GAME_ID = "2019020876";

    private static String getStage() {

        final String stageEnvironmentVariable = System.getenv("Stage");
        return stageEnvironmentVariable == null ? "personal" : stageEnvironmentVariable;
    }

    @BeforeClass
    public static void setUpIntegrationTestingQueue() {

        final SqsQueueMetadata sqsQueueMetadata = createQueue();
        subscribeQueueToTopic(sqsQueueMetadata);
    }

    private static SqsQueueMetadata createQueue() {

        final CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(QUEUE_NAME)
                .build();
        final CreateQueueResponse createQueueResponse = SQS_CLIENT.createQueue(createQueueRequest);
        final SqsQueueMetadata sqsQueueMetadata = new SqsQueueMetadata(createQueueResponse.queueUrl());
        final String queueUrl = sqsQueueMetadata.getQueueUrl();

        final GetQueueAttributesRequest getQueueAttributesRequest = GetQueueAttributesRequest.builder()
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .queueUrl(queueUrl)
                .build();
        final GetQueueAttributesResponse getQueueAttributesResponse = SQS_CLIENT.getQueueAttributes(getQueueAttributesRequest);
        sqsQueueMetadata.setQueueArn(getQueueAttributesResponse.attributes().get(QueueAttributeName.QUEUE_ARN));

        final String policy = format(
             "{" +
                 "\"Statement\": [" +
                     "{" +
                         "\"Effect\": \"Allow\"," +
                         "\"Principal\": \"*\"," +
                         "\"Action\": \"sqs:SendMessage\"," +
                         "\"Resource\": \"%s\"," +
                         "\"Condition\": {" +
                             "\"ArnEquals\": {" +
                                 "\"aws:SourceArn\": \"%s\"" +
                             "}" +
                         "}" +
                     "}" +
                 "]" +
            "}",
             sqsQueueMetadata.getQueueArn(),
             TOPIC_ARN);
        final SetQueueAttributesRequest setQueueAttributesRequest = SetQueueAttributesRequest.builder()
                .attributes(ImmutableMap.of(QueueAttributeName.POLICY, policy))
                .queueUrl(queueUrl)
                .build();
        final SetQueueAttributesResponse setQueueAttributesResponse = SQS_CLIENT.setQueueAttributes(setQueueAttributesRequest);
        return sqsQueueMetadata;
    }

    private static void subscribeQueueToTopic(final SqsQueueMetadata sqsQueueMetadata) {

        final SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .topicArn(TOPIC_ARN)
                .protocol("sqs")
                .attributes(ImmutableMap.of("RawMessageDelivery", "true"))
                .endpoint(sqsQueueMetadata.getQueueArn())
                .build();
        final SubscribeResponse subscribeResponse = SNS_CLIENT.subscribe(subscribeRequest);
    }

    @Before
    public void setUpTest() {

        purgeQueue();
        resetPlayByPlayProcessingItem();
    }

    private void purgeQueue() {

        final String queueUrl = getQueueUrl();
        final PurgeQueueRequest purgeQueueRequest = PurgeQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();
        final PurgeQueueResponse purgeQueueResponse = SQS_CLIENT.purgeQueue(purgeQueueRequest);
    }

    private static String getQueueUrl() {

        final GetQueueUrlRequest getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName(QUEUE_NAME)
                .build();
        final GetQueueUrlResponse getQueueUrlResponse = SQS_CLIENT.getQueueUrl(getQueueUrlRequest);
        return getQueueUrlResponse.queueUrl();
    }

    @Test
    public void test_handle_request_publishes_all_events_for_game() throws IOException {

        final Map<String, AttributeValue> item = new HashMap<>();
        item.put("compositeGameId", AttributeValue.builder().s(COMPOSITE_GAME_ID).build());
        item.put("lastProcessedEventIndex", AttributeValue.builder().n("0").build());
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(DYNAMO_DB_TABLE_NAME)
                .item(item)
                .build();
        final PutItemResponse putItemResponse = DYNAMO_DB_CLIENT.putItem(putItemRequest);

        final InvokeRequest invokeRequest = InvokeRequest.builder()
                .functionName(LAMBDA_FUNCTION_NAME)
                .payload(generatePayloadForLambdaInvokeFromGameId())
                .build();

        final InvokeResponse invokeResponse = LAMBDA_CLIENT.invoke(invokeRequest);

        assertThat(invokeResponse.statusCode(), is(200));
        final String queueUrl = getQueueUrl();
        verifyLambdaPublishedAllEventsForGameId(queueUrl);
    }

    private SdkBytes generatePayloadForLambdaInvokeFromGameId() {

        return SdkBytes.fromString(format("{\"gameId\":\"%s\"}", GAME_ID), StandardCharsets.UTF_8);
    }

    private void verifyLambdaPublishedAllEventsForGameId(final String queueUrl) throws IOException {

        // Get the authoritative source of events for the game
        final NhlLiveGameFeedResponse liveGameFeedResponse = NHL_CLIENT.getPlayByPlayData(Integer.parseInt(GAME_ID));
        final List<Play> allPlays = liveGameFeedResponse.getLiveData().getPlays().getAllPlays();
        final Set<String> playProcessingSet = allPlays.stream()
                .map(play -> play.getResult().getEventCode())
                .collect(Collectors.toSet());
        assertTrue(allPlays.size() > 0);
        assertThat(playProcessingSet.size(), is(allPlays.size())); // Verifies the eventCodes are unique which is suspected/potentially confirmed

        final long startTime = System.currentTimeMillis();
        final long waitTime = 1000 * 60 * 5;
        final long endTime = startTime + waitTime;
        do {

            final ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .maxNumberOfMessages(10)
                    .queueUrl(queueUrl)
                    .waitTimeSeconds(5)
                    .build();
            final ReceiveMessageResponse receiveMessageResponse = SQS_CLIENT.receiveMessage(receiveMessageRequest);
            final List<Message> messages = receiveMessageResponse.messages();
            for (final Message message : messages) {

                final String messageBody = message.body();
                assertThat(messageBody, is(notNullValue()));
                final PlayEvent playEvent = OBJECT_MAPPER.readValue(messageBody, PlayEvent.class);
                final String playEventCode = playEvent.getPlay().getResult().getEventCode();
                assertThat(playEventCode, is(notNullValue()));
                assertTrue(playProcessingSet.contains(playEventCode));
                playProcessingSet.remove(playEventCode);
            }
        }
        while (playProcessingSet.size() > 0 && System.currentTimeMillis() < endTime);

        assertThat("The number of messages published by the EventPublisher Lambda is less than the number of messages in the NHL" +
                "API's Gamefeed response", playProcessingSet.size(), is(0));
    }

    @After
    public void cleanUpTest() {

        resetPlayByPlayProcessingItem();
    }

    private static void resetPlayByPlayProcessingItem() {

        try {

            final DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                    .key(ImmutableMap.of("compositeGameId", AttributeValue.builder().s(COMPOSITE_GAME_ID).build()))
                    .tableName(DYNAMO_DB_TABLE_NAME)
                    .build();
            final DeleteItemResponse deleteItemResponse = DYNAMO_DB_CLIENT.deleteItem(deleteItemRequest);
        }
        catch (ResourceNotFoundException e) {

            // Do nothing, item is already deleted/does not exist
        }
    }

    @AfterClass
    public static void cleanUpIntegrationTestingQueue() {

        final ListSubscriptionsByTopicRequest listSubscriptionsByTopicRequest = ListSubscriptionsByTopicRequest.builder()
                .topicArn(TOPIC_ARN)
                .build();
        final ListSubscriptionsByTopicResponse listSubscriptionsByTopicResponse =
                SNS_CLIENT.listSubscriptionsByTopic(listSubscriptionsByTopicRequest);
        final Optional<Subscription> subscription = listSubscriptionsByTopicResponse.subscriptions().stream()
                .filter(s -> s.endpoint().contains(QUEUE_NAME))
                .findFirst();
        final String subscriptionArn = subscription.get().subscriptionArn();
        final UnsubscribeRequest unsubscribeRequest = UnsubscribeRequest.builder()
                .subscriptionArn(subscriptionArn)
                .build();
        final UnsubscribeResponse unsubscribeResponse = SNS_CLIENT.unsubscribe(unsubscribeRequest);
        final String queueUrl = getQueueUrl();
        deleteQueue(queueUrl);
    }

    private static void deleteQueue(final String queueUrl) {

        final DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();
        final DeleteQueueResponse deleteQueueResponse = SQS_CLIENT.deleteQueue(deleteQueueRequest);
    }

}
