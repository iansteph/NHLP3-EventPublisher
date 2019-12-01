package iansteph.nhlp3.eventpublisher.proxy;

import com.google.common.hash.Hashing;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class DynamoDbProxy {

    private final DynamoDbClient dynamoDbClient;
    private final String nhlPlayByPlayProcessingDynamoDbTableName;

    private static final Logger logger = LogManager.getLogger(DynamoDbProxy.class);

    public DynamoDbProxy(
            final DynamoDbClient dynamoDbClient,
            final String nhlPlayByPlayProcessingDynamoDbTableName) {

        this.dynamoDbClient = dynamoDbClient;
        this.nhlPlayByPlayProcessingDynamoDbTableName = nhlPlayByPlayProcessingDynamoDbTableName;
    }

    public Map<String, AttributeValue> getNhlPlayByPlayProcessingItem(final EventPublisherRequest request) {

        try {

            checkNotNull(request, format("GameId %s | EventPublisherRequest must not be null passed as parameter when calling " +
                    "DynamoDbProxy::getNhlPlayByPlayProcessingItem", request.getGameId()));
            final String compositeGameId = generateCompositeGameId(request);
            logger.info(format("Retrieving item for primary key %s from %s", compositeGameId,
                    nhlPlayByPlayProcessingDynamoDbTableName));
            Map<String, AttributeValue> primaryKey = new HashMap<>();
            primaryKey.put("compositeGameId", AttributeValue.builder().s(compositeGameId).build());
            final GetItemRequest getItemRequest = GetItemRequest.builder()
                    .tableName(nhlPlayByPlayProcessingDynamoDbTableName)
                    .key(primaryKey)
                    .build();
            final Map<String, AttributeValue> retrievedItem = dynamoDbClient.getItem(getItemRequest).item();
            checkNotNull(retrievedItem, format("GameId %s | Retrieved item was null for EventPublisherRequest: %s", request.getGameId(),
                    request));
            logger.info(format("Retrieved item %s from %s", retrievedItem, nhlPlayByPlayProcessingDynamoDbTableName));
            return retrievedItem;
        }
        catch (NullPointerException e) {

            logger.error(e);
            throw e;
        }
    }

    public Map<String, AttributeValue> updateNhlPlayByPlayProcessingItem(
            final Map<String, AttributeValue> item,
            final Optional<NhlLiveGameFeedResponse> nhlLiveGameFeedResponse
    ) {

        try {

            checkNotNull(item, "Item must be non-null when passed as parameter when calling " +
                    "DynamoDbProxy::updateNhlPlayByPlayProcessingItem");
            checkNotNull(nhlLiveGameFeedResponse, "NhlLiveGameFeedResponse must be non-null when passed as parameter when " +
                    "calling DynamoDbProxy::updateNhlPlayByPlayProcessingItem");
            final Map<String, AttributeValue> itemToUpdate = new HashMap<>(item);
            if (nhlLiveGameFeedResponse.isPresent()) {

                final NhlLiveGameFeedResponse response = nhlLiveGameFeedResponse.get();
                itemToUpdate.put("lastProcessedEventIndex", AttributeValue.builder()
                        .n(String.valueOf(response.getLiveData().getPlays().getCurrentPlay().getAbout().getEventIdx()))
                        .build());
                itemToUpdate.put("inIntermission", AttributeValue.builder()
                        .bool(response.getLiveData().getLinescore().getIntermissionInfo().isInIntermission())
                        .build());
            }
            itemToUpdate.put("lastProcessedTimeStamp", AttributeValue.builder().s(createNewLastProcessedTimestamp()).build());
            final PutItemResponse response = dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(nhlPlayByPlayProcessingDynamoDbTableName)
                    .item(itemToUpdate)
                    .build());
            final Map<String, AttributeValue> responseAttributes = response.attributes();
            logger.info(format("Saved updated item to DynamoDB. Response: %s", responseAttributes));
            return responseAttributes;
        }
        catch (NullPointerException e) {

            logger.error(e);
            throw e;
        }
    }

    private String generateCompositeGameId(final EventPublisherRequest request) {

        final int gameId = request.getGameId();
        final String hashedGameId = Hashing.murmur3_128().hashInt(gameId).toString();
        final String compositeGameId = String.format("%s~%s", hashedGameId, gameId);
        logger.info(format("CompositeGameId is %s for GameId %s and EventPublisherRequest %s", compositeGameId, gameId, request));
        return compositeGameId;
    }

    private String createNewLastProcessedTimestamp() {
        final LocalDateTime nowDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        final LocalDate nowDate = nowDateTime.toLocalDate();
        final LocalTime nowTime = nowDateTime.toLocalTime();
        return format("%d%02d%02d_%02d%02d%02d", nowDate.getYear(), nowDate.getMonthValue(), nowDate.getDayOfMonth(), nowTime.getHour(),
                nowTime.getMinute(), nowTime.getSecond());
    }
}
