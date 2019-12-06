package iansteph.nhlp3.eventpublisher.proxy;

import com.google.common.hash.Hashing;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.GameData;
import iansteph.nhlp3.eventpublisher.model.nhl.gamedata.Status;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.Linescore;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.LiveData;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.Plays;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.IntermissionInfo;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.About;
import iansteph.nhlp3.eventpublisher.model.nhl.metadata.MetaData;
import org.junit.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DyanmoDbProxyTest extends UnitTestBase {

    private DynamoDbClient mockDynamoDbClient = mock(DynamoDbClient.class);
    private DynamoDbProxy proxy = new DynamoDbProxy(mockDynamoDbClient, "someDynamoDBTableName");

    @Test
    public void testGetNhlPlayByPlayProcessingItemIsSuccessful() {

        final int gameId = EventPublisherRequest.getGameId();
        final Map<String, AttributeValue> nhlPlayByPlayProcessingItem = new HashMap<>();
        final String expectedCompositeGameId = String.format("%s~%s", Hashing.murmur3_128().hashInt(gameId), gameId);
        nhlPlayByPlayProcessingItem.put("compositeGameId", AttributeValue.builder().s(expectedCompositeGameId).build());
        final GetItemResponse getItemResponse = GetItemResponse.builder().item(nhlPlayByPlayProcessingItem).build();
        when(mockDynamoDbClient.getItem(any(GetItemRequest.class))).thenReturn(getItemResponse);

        final Map<String, AttributeValue> response = proxy.getNhlPlayByPlayProcessingItem(EventPublisherRequest);

        final AttributeValue responseCompositeId = response.get("compositeGameId");
        assertThat(response, is(notNullValue()));
        assertThat(response, is(not(Collections.emptyMap())));
        assertThat(responseCompositeId, is(notNullValue()));
        assertThat(responseCompositeId.s(), is(expectedCompositeGameId));
    }

    @Test(expected = NullPointerException.class)
    public void testGetNhlPlayByPlayProcessingItemThrowsNullPointerExceptionWhenRequestIsNull() {

        proxy.getNhlPlayByPlayProcessingItem(null);
    }

    @Test
    public void testUpdateNhlPlayByPlayProcessingItemIsSuccessful() {

        final String compositeGameId = "compositeGameId";
        final Map<String, AttributeValue> nhlPlayByPlayProcessingItem = new HashMap<>();
        nhlPlayByPlayProcessingItem.put("compositeGameId", AttributeValue.builder().s(compositeGameId).build());
        nhlPlayByPlayProcessingItem.put("inIntermission", AttributeValue.builder().bool(false).build());
        nhlPlayByPlayProcessingItem.put("lastProcessedTimeStamp", AttributeValue.builder().s("20191025_004123").build());
        nhlPlayByPlayProcessingItem.put("lastProcessedEventIndex", AttributeValue.builder().n("100").build());
        final Status status = new Status();
        status.setAbstractGameState("Final");
        final GameData gameData = new GameData();
        gameData.setStatus(status);
        final LiveData liveData = new LiveData();
        final Linescore lineScore = new Linescore();
        final IntermissionInfo intermissionInfo = new IntermissionInfo();
        intermissionInfo.setInIntermission(true);
        lineScore.setIntermissionInfo(intermissionInfo);
        liveData.setLinescore(lineScore);
        final Plays plays = new Plays();
        final About about = new About();
        final int expectedEventIndex = 110;
        about.setEventIdx(expectedEventIndex);
        final Play currentPlay = new Play();
        currentPlay.setAbout(about);
        plays.setCurrentPlay(currentPlay);
        liveData.setPlays(plays);
        final MetaData metaData = new MetaData();
        final String expectedTimestamp = "20191025_004200";
        metaData.setTimeStamp(expectedTimestamp);
        final NhlLiveGameFeedResponse nhlLiveGameFeedResponse = new NhlLiveGameFeedResponse();
        nhlLiveGameFeedResponse.setGameData(gameData);
        nhlLiveGameFeedResponse.setMetaData(metaData);
        nhlLiveGameFeedResponse.setLiveData(liveData);
        final Map<String, AttributeValue> expectedItem = new HashMap<>();
        expectedItem.put("compositeGameId", AttributeValue.builder().s(compositeGameId).build());
        expectedItem.put("inIntermission", AttributeValue.builder().bool(true).build());
        expectedItem.put("lastProcessedTimeStamp", AttributeValue.builder().s(expectedTimestamp).build());
        expectedItem.put("lastProcessedEventIndex", AttributeValue.builder().n(String.valueOf(expectedEventIndex)).build());
        when(mockDynamoDbClient.putItem(any(PutItemRequest.class))).thenReturn(PutItemResponse.builder().attributes(expectedItem).build());

        final Map<String, AttributeValue> response = proxy.updateNhlPlayByPlayProcessingItem(nhlPlayByPlayProcessingItem,
                Optional.of(nhlLiveGameFeedResponse));

        assertThat(response, is(notNullValue()));
        assertThat(response, is(not(Collections.emptyMap())));
        final AttributeValue responseCompositeGameId = response.get("compositeGameId");
        assertThat(responseCompositeGameId, is(notNullValue()));
        assertThat(responseCompositeGameId.s(), is(compositeGameId));
        final AttributeValue responseInIntermission = response.get("inIntermission");
        assertThat(responseInIntermission, is(notNullValue()));
        assertTrue(responseInIntermission.bool());
        final AttributeValue responseLastProcessedTimeStamp = response.get("lastProcessedTimeStamp");
        assertThat(responseLastProcessedTimeStamp, is(notNullValue()));
        final AttributeValue responseLastProcessedEventIndex = response.get("lastProcessedEventIndex");
        assertThat(responseLastProcessedEventIndex, is(notNullValue()));
        assertThat(Integer.parseInt(responseLastProcessedEventIndex.n()), is(expectedEventIndex));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNhlPlayByPlayProcessingItemThrowsNullPointerExceptionWhenItemToUpdateIsNull() {

        proxy.updateNhlPlayByPlayProcessingItem(null, Optional.of(new NhlLiveGameFeedResponse()));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNhlPlayByPlayProcessingItemThrowsNullPointerExceptionWhenNhlLiveGameFeedResponseInputIsNull() {

        proxy.updateNhlPlayByPlayProcessingItem(new HashMap<String, AttributeValue>(), null);
    }
}
