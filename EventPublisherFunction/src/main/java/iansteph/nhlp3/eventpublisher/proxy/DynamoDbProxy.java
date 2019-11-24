package iansteph.nhlp3.eventpublisher.proxy;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.hash.Hashing;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class DynamoDbProxy {

    private final DynamoDBMapper dynamoDBMapper;

    private static final Logger logger = LogManager.getLogger(DynamoDbProxy.class);

    public DynamoDbProxy(final DynamoDBMapper dynamoDbMapper) {

        this.dynamoDBMapper = dynamoDbMapper;
    }

    public NhlPlayByPlayProcessingItem getNhlPlayByPlayProcessingItem(final EventPublisherRequest request) {

        try {

            checkNotNull(request, "EventPublisherRequest must not be null passed as parameter when calling " +
                    "DynamoDbProxy::getNhlPlayByPlayProcessingItem");
            final NhlPlayByPlayProcessingItem item = new NhlPlayByPlayProcessingItem();
            final String compositeGameId = generateCompositeGameId(request);
            item.setCompositeGameId(compositeGameId);
            logger.info(format("Retrieving NhlPlayByPlayProcessingItem for primary key: %s", compositeGameId));
            final NhlPlayByPlayProcessingItem retrievedItem = dynamoDBMapper.load(item);
            checkNotNull(retrievedItem, format("Retrieved item was null for EventPublisherRequest: %s", request));
            logger.info(format("Retrieved NhlPlayByPlayProcessingItem: %s", retrievedItem));
            return retrievedItem;
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

    public NhlPlayByPlayProcessingItem updateNhlPlayByPlayProcessingItem(
            final NhlPlayByPlayProcessingItem itemToUpdate,
            final Optional<NhlLiveGameFeedResponse> nhlLiveGameFeedResponse
    ) {

        try {

            checkNotNull(itemToUpdate, "Item must be non-null when passed as parameter when calling " +
                    "DynamoDbProxy::updateNhlPlayByPlayProcessingItem");
            checkNotNull(nhlLiveGameFeedResponse, "NhlLiveGameFeedResponse must be non-null when passed as parameter when " +
                    "calling DynamoDbProxy::updateNhlPlayByPlayProcessingItem");
            if (nhlLiveGameFeedResponse.isPresent()) {

                final NhlLiveGameFeedResponse response = nhlLiveGameFeedResponse.get();
                itemToUpdate.setLastProcessedEventIndex(response.getLiveData().getPlays().getCurrentPlay().getAbout().getEventIdx());
                itemToUpdate.setInIntermission(response.getLiveData().getLinescore().getIntermissionInfo().isInIntermission());
            }
            itemToUpdate.setLastProcessedTimeStamp(createNewLastProcessedTimestamp());
            dynamoDBMapper.save(itemToUpdate);
            logger.info(format("Saved updated NhlPlayByPlayProcessingItem to DyanmoDB: %s", itemToUpdate));
            return itemToUpdate;
        }
        catch (NullPointerException e) {

            logger.error(e);
            throw e;
        }
    }

    private String createNewLastProcessedTimestamp() {
        final LocalDateTime nowDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        final LocalDate nowDate = nowDateTime.toLocalDate();
        final LocalTime nowTime = nowDateTime.toLocalTime();
        return format("%d%02d%02d_%02d%02d%02d", nowDate.getYear(), nowDate.getMonthValue(), nowDate.getDayOfMonth(), nowTime.getHour(),
                nowTime.getMinute(), nowTime.getSecond());
    }
}
