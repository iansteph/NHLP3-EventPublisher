package iansteph.nhl.eventpublisher.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Handler for requests to Lambda function.
 */
public class EventPublisherHandler implements RequestHandler<Object, Object> {

    public Object handleRequest(final Object input, final Context context) {
        return new Object();
    }
}
