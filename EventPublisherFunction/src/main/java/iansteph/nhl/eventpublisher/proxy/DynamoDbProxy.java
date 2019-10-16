package iansteph.nhl.eventpublisher.proxy;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.hash.Hashing;
import iansteph.nhl.eventpublisher.handler.EventPublisherRequest;
import iansteph.nhl.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;

import static com.google.common.base.Preconditions.checkNotNull;

public class DynamoDbProxy {

    private DynamoDBMapper dynamoDBMapper;

    public DynamoDbProxy(final DynamoDBMapper dynamoDbMapper) {
        this.dynamoDBMapper = dynamoDbMapper;
    }

    public NhlPlayByPlayProcessingItem getNhlPlayByPlayProcessingItemForGameId(final EventPublisherRequest request) {
        checkNotNull(request);

        final NhlPlayByPlayProcessingItem item = new NhlPlayByPlayProcessingItem();
        final String compositeGameId = generateCompositeGameId(request);
        item.setCompositeGameId(compositeGameId);

        return dynamoDBMapper.load(item);
    }

    private String generateCompositeGameId(final EventPublisherRequest request) {
        final int gameId = request.getGameId();
        final String hashedGameId = Hashing.murmur3_128().hashInt(gameId).toString();
        return String.format("%s~%s", hashedGameId, gameId);
    }
}
