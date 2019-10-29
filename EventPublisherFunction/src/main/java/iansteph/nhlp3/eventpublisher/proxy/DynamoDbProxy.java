package iansteph.nhlp3.eventpublisher.proxy;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.google.common.hash.Hashing;
import iansteph.nhlp3.eventpublisher.handler.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class DynamoDbProxy {

    private final DynamoDBMapper dynamoDBMapper;

    public DynamoDbProxy(final DynamoDBMapper dynamoDbMapper) {
        this.dynamoDBMapper = dynamoDbMapper;
    }

    public NhlPlayByPlayProcessingItem getNhlPlayByPlayProcessingItem(final EventPublisherRequest request) {
        checkNotNull(request);

        final NhlPlayByPlayProcessingItem item = new NhlPlayByPlayProcessingItem();
        final String compositeGameId = generateCompositeGameId(request);
        item.setCompositeGameId(compositeGameId);
        final NhlPlayByPlayProcessingItem retrievedItem = dynamoDBMapper.load(item);

        checkNotNull(retrievedItem);
        System.out.println(format("Retrieved NhlPlayByPlayProcessingItem: %s", retrievedItem));
        return retrievedItem;
    }

    private String generateCompositeGameId(final EventPublisherRequest request) {
        final int gameId = request.getGameId();
        final String hashedGameId = Hashing.murmur3_128().hashInt(gameId).toString();
        return String.format("%s~%s", hashedGameId, gameId);
    }

    public NhlPlayByPlayProcessingItem updateNhlPlayByPlayProcessingItem(final NhlPlayByPlayProcessingItem itemToUpdate,
            final NhlLiveGameFeedResponse nhlLiveGameFeedResponse) {
        checkNotNull(itemToUpdate);
        checkNotNull(nhlLiveGameFeedResponse);

        itemToUpdate.setHasGameEnded(nhlLiveGameFeedResponse.getGameData().getStatus().isGameEnded());
        itemToUpdate.setIsIntermission(nhlLiveGameFeedResponse.getLiveData().getLinescore().getIntermissionInfo().isInIntermission());
        itemToUpdate.setLastProcessedEventIndex(nhlLiveGameFeedResponse.getLiveData().getPlays().getCurrentPlay().getAbout().getEventIdx());
        itemToUpdate.setLastProcessedTimeStamp(nhlLiveGameFeedResponse.getMetaData().getTimeStamp());

        dynamoDBMapper.save(itemToUpdate);

        return itemToUpdate;
    }
}
