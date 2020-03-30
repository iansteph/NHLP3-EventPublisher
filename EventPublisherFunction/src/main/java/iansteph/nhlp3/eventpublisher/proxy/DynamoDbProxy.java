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
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class DynamoDbProxy {

    private final DynamoDbClient dynamoDbClient;
    private final String nhlPlayByPlayProcessingDynamoDbTableName;

    private static final String COMPOSITE_GAME_ID_PRIMARY_KEY = "compositeGameId";
    private static final String PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME = "publishedEventCodeSet";
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
            final String compositeGameId = generateCompositeGameId(request.getGameId());
            logger.info(format("Retrieving item for primary key %s from %s", compositeGameId,
                    nhlPlayByPlayProcessingDynamoDbTableName));
            Map<String, AttributeValue> primaryKey = new HashMap<>();
            primaryKey.put(COMPOSITE_GAME_ID_PRIMARY_KEY, AttributeValue.builder().s(compositeGameId).build());
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

    private String generateCompositeGameId(final int gamePk) {

        final String hashedGameId = Hashing.murmur3_128().hashInt(gamePk).toString();
        final String compositeGameId = String.format("%s~%s", hashedGameId, gamePk);
        logger.info(format("%s is %s for gamePk %s", COMPOSITE_GAME_ID_PRIMARY_KEY, compositeGameId, gamePk));
        return compositeGameId;
    }

    public void updatePublishedEventCodeSet(
            final int gamePk,
            final Set<String> publishedEventCodesToAdd
    ) {

        final Map<String, AttributeValue> primaryKey = new HashMap<>();
        primaryKey.put(COMPOSITE_GAME_ID_PRIMARY_KEY, AttributeValue.builder().s(generateCompositeGameId(gamePk)).build());
        final String publishedEventCodeSetAttributeName = "#publishedEventCodeSet";
        final Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put(publishedEventCodeSetAttributeName, PUBLISHED_EVENT_CODE_SET_ATTRIBUTE_NAME);
        final String publishedEventCodeSetAttributeValue = ":ecs";
        final Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(publishedEventCodeSetAttributeValue, AttributeValue.builder().ss(publishedEventCodesToAdd).build());
        final UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(nhlPlayByPlayProcessingDynamoDbTableName)
                .key(primaryKey)
                .updateExpression(format("ADD %s %s", publishedEventCodeSetAttributeName, publishedEventCodeSetAttributeValue))
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .returnValues(ReturnValue.UPDATED_NEW)
                .build();
        final UpdateItemResponse updateItemResponse = dynamoDbClient.updateItem(updateItemRequest);
        logger.info(format("Updated item to DynamoDB. Response: %s", updateItemResponse));
    }
}
