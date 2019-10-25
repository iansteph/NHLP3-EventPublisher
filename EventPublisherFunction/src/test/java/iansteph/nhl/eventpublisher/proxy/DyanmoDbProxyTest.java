package iansteph.nhl.eventpublisher.proxy;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.hash.Hashing;
import iansteph.nhl.eventpublisher.UnitTestBase;
import iansteph.nhl.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DyanmoDbProxyTest extends UnitTestBase {

    private DynamoDBMapper mockDynamoDBMapper = mock(DynamoDBMapper.class);
    private DynamoDbProxy proxy = new DynamoDbProxy(mockDynamoDBMapper);

    @Test
    public void testGetNhlPlayByPlayProcessingItemForGameIdIsSuccessful() {
        when(mockDynamoDBMapper.load(any(NhlPlayByPlayProcessingItem.class))).thenReturn(new NhlPlayByPlayProcessingItem());
        final ArgumentCaptor<NhlPlayByPlayProcessingItem> argumentCaptor = ArgumentCaptor.forClass(NhlPlayByPlayProcessingItem.class);
        final int gameId = EventPublisherRequest.getGameId();
        final String expectedCompositeGameId = String.format("%s~%s", Hashing.murmur3_128().hashInt(EventPublisherRequest.getGameId()),
                gameId);

        final NhlPlayByPlayProcessingItem response = proxy.getNhlPlayByPlayProcessingItemForGameId(EventPublisherRequest);

        verify(mockDynamoDBMapper, times(1)).load(argumentCaptor.capture());
        final NhlPlayByPlayProcessingItem nhlPlayByPlayProcessingItem = argumentCaptor.getValue();
        assertThat(nhlPlayByPlayProcessingItem.getCompositeGameId(), is(expectedCompositeGameId));
    }

    @Test(expected = NullPointerException.class)
    public void testGetNhlPlayByPlayProcessingItemForGameIdThrowsNullPointerExceptionWhenRequestIsNull() {
        proxy.getNhlPlayByPlayProcessingItemForGameId(null);
    }
}
