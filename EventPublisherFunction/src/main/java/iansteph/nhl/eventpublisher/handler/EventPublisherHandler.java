package iansteph.nhl.eventpublisher.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import iansteph.nhl.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhl.eventpublisher.model.dynamo.NhlPlayByPlayProcessingItem;
import iansteph.nhl.eventpublisher.proxy.DynamoDbProxy;
import iansteph.nhl.eventpublisher.proxy.NhlPlayByPlayProxy;
import org.springframework.web.client.RestTemplate;

/**
 * Handler for requests to Lambda function.
 */
public class EventPublisherHandler implements RequestHandler<EventPublisherRequest, Object> {

    private DynamoDbProxy dynamoDbProxy;
    private NhlPlayByPlayProxy nhlPlayByPlayProxy;


    public EventPublisherHandler() {
        final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().build();
        final DynamoDBMapper dynamoDbMapper = new DynamoDBMapper(amazonDynamoDB);
        this.dynamoDbProxy = new DynamoDbProxy(dynamoDbMapper);

        final NhlPlayByPlayClient nhlPlayByPlayClient = new NhlPlayByPlayClient(new RestTemplate());
        this.nhlPlayByPlayProxy = new NhlPlayByPlayProxy(nhlPlayByPlayClient);
    }

    public EventPublisherHandler(final DynamoDbProxy dynamoDbProxy, final NhlPlayByPlayProxy nhlPlayByPlayProxy) {
        this.dynamoDbProxy = dynamoDbProxy;
        this.nhlPlayByPlayProxy = nhlPlayByPlayProxy;
    }

    public Object handleRequest(final EventPublisherRequest eventPublisherRequest, final Context context) {

        // TODO | Call DynamoDB to get the last processed event
        final NhlPlayByPlayProcessingItem nhlPlayByPlayProcessingItem =
                dynamoDbProxy.getNhlPlayByPlayProcessingItemForGameId(eventPublisherRequest);
        final String lastProcessedTimestamp = nhlPlayByPlayProcessingItem.getLastProcessedTimeStamp();

        // TODO | Call the NHL endpoint to get all of the events that should be processed
        nhlPlayByPlayProxy.getPlayByPlayEventsSinceLastProcessedTimestamp(lastProcessedTimestamp, eventPublisherRequest);

        // TODO | For each play publish a message

        // TODO | Write to DynamoDB of the last event published
        return new Object();
    }
}
