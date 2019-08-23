package iansteph.nhl.eventpublisher.handler;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class EventPublisherHandlerTest {

    @Test
    public void successfulResponse() {
        EventPublisherHandler eventPublisherHandler = new EventPublisherHandler();
        Object result = eventPublisherHandler.handleRequest(null, null);
        assertNotNull(result);
    }
}
