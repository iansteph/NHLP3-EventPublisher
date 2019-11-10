package iansteph.nhlp3.eventpublisher.proxy;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.hash.Hashing;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DyanmoDbProxyTest extends UnitTestBase {

    private DynamoDBMapper mockDynamoDBMapper = mock(DynamoDBMapper.class);
    private DynamoDbProxy proxy = new DynamoDbProxy(mockDynamoDBMapper);

    @Test
    public void testGetNhlPlayByPlayProcessingItemIsSuccessful() {
        final int gameId = EventPublisherRequest.getGameId();
        final NhlPlayByPlayProcessingItem nhlPlayByPlayProcessingItem = new NhlPlayByPlayProcessingItem();
        final String expectedCompositeGameId = String.format("%s~%s", Hashing.murmur3_128().hashInt(gameId), gameId);
        nhlPlayByPlayProcessingItem.setCompositeGameId(expectedCompositeGameId);
        when(mockDynamoDBMapper.load(any(NhlPlayByPlayProcessingItem.class))).thenReturn(nhlPlayByPlayProcessingItem);

        final NhlPlayByPlayProcessingItem response = proxy.getNhlPlayByPlayProcessingItem(EventPublisherRequest);

        assertThat(response.getCompositeGameId(), is(expectedCompositeGameId));
    }

    @Test(expected = NullPointerException.class)
    public void testGetNhlPlayByPlayProcessingItemThrowsNullPointerExceptionWhenRequestIsNull() {
        proxy.getNhlPlayByPlayProcessingItem(null);
    }

    @Test
    public void testUpdateNhlPlayByPlayProcessingItemIsSuccessful() {
        final NhlPlayByPlayProcessingItem nhlPlayByPlayProcessingItem = new NhlPlayByPlayProcessingItem();
        final String compositeGameId = "compositeGameId";
        nhlPlayByPlayProcessingItem.setCompositeGameId(compositeGameId);
        nhlPlayByPlayProcessingItem.setInIntermission(false);
        nhlPlayByPlayProcessingItem.setLastProcessedTimeStamp("20191025_004123");
        nhlPlayByPlayProcessingItem.setLastProcessedEventIndex(100);
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

        final NhlPlayByPlayProcessingItem response = proxy.updateNhlPlayByPlayProcessingItem(nhlPlayByPlayProcessingItem,
                nhlLiveGameFeedResponse);

        assertThat(response, is(notNullValue()));
        assertThat(response.getCompositeGameId(), is(compositeGameId));
        assertTrue(response.inIntermission());
        assertThat(response.getLastProcessedTimeStamp(), is(expectedTimestamp));
        assertThat(response.getLastProcessedEventIndex(), is(expectedEventIndex));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNhlPlayByPlayProcessingItemThrowsNullPointerExceptionWhenItemToUpdateIsNull() {
        proxy.updateNhlPlayByPlayProcessingItem(null, new NhlLiveGameFeedResponse());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNhlPlayByPlayProcessingItemThrowsNullPointerExceptionWhenNhlLiveGameFeedResponseInputIsNull() {
        proxy.updateNhlPlayByPlayProcessingItem(new NhlPlayByPlayProcessingItem(), null);
    }
}
