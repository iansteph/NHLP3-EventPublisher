package iansteph.nhlp3.eventpublisher.proxy;

import com.google.common.hash.Hashing;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import org.junit.Test;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DyanmoDbProxyTest extends UnitTestBase {

    private DynamoDbClient mockDynamoDbClient = mock(DynamoDbClient.class);
    private DynamoDbProxy proxy = new DynamoDbProxy(mockDynamoDbClient, "someDynamoDBTableName");

    @Test
    public void test_getNhlPlayByPlayProcessingItem_is_successful() {

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
        verify(mockDynamoDbClient, times(1)).getItem(any(GetItemRequest.class));
    }

    @Test(expected = NullPointerException.class)
    public void test_getNhlPlayByPlayProcessingItem_throws_NullPointerException_when_request_is_null() {

        proxy.getNhlPlayByPlayProcessingItem(null);
    }

    @Test
    public void test_updatePublishedEventCodeSet_is_successful() {

        when(mockDynamoDbClient.updateItem(any(UpdateItemRequest.class))).thenReturn(any());
        final int gameId = EventPublisherRequest.getGameId();
        final Set<String> publishedEventCodesToAdd = new HashSet<>();
        publishedEventCodesToAdd.add("TEST1");

        proxy.updatePublishedEventCodeSet(gameId, publishedEventCodesToAdd);

        verify(mockDynamoDbClient, times(1)).updateItem(any(UpdateItemRequest.class));
    }
}
