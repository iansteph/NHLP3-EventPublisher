package iansteph.nhl.eventpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Handler for requests to Lambda function.
 */
public class EventPublisherHandler implements RequestHandler<EventPublisherRequest, Object> {

    public Object handleRequest(final EventPublisherRequest eventPublisherRequest, final Context context) {

        // TODO:  This is how the gameId for the EventPublisher to query the NHL Play-by-Play API for
        // TODO:    Reference: https://docs.aws.amazon.com/en_pv/lambda/latest/dg/java-handler-io-type-pojo.html
        eventPublisherRequest.getGameId();
        return new Object();
    }
}
