package iansteph.nhlp3.eventpublisher.proxy;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.Result;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventPublisherProxyTest extends UnitTestBase {

    private final AmazonSNS mockAmazonSnsClient = mock(AmazonSNS.class);
    private final SafeObjectMapper mockObjectMapper = mock(SafeObjectMapper.class);
    private final EventPublisherProxy eventPublisherProxy = new EventPublisherProxy(mockAmazonSnsClient, mockObjectMapper);

    @Test
    public void testPublishIsSuccessful() {

        final Play play = new Play();
        final Result result = new Result();
        result.setEventTypeId("Game_End");
        play.setResult(result);
        final PlayEvent playEvent = new PlayEvent().withPlay(play).withGamePk(GameId);
        when(mockAmazonSnsClient.publish(any(PublishRequest.class))).thenReturn(new PublishResult());
        when(mockObjectMapper.writeValueAsString(playEvent)).thenReturn(playEvent.toString());

        eventPublisherProxy.publish(playEvent, 1, 2);

        verify(mockAmazonSnsClient, times(1)).publish(any(PublishRequest.class));
    }

    @Test(expected = RuntimeException.class)
    public void testPublishCatchesJsonProcessingExceptionFromObjectMapperAndThrowsRuntimeException() {

        when(mockAmazonSnsClient.publish(any(PublishRequest.class))).thenReturn(new PublishResult());

        // Create an anonymous exception of type JsonProcessingException with "{}" because the exception's constructors are protected
        when(mockObjectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Oops!"){});

        eventPublisherProxy.publish(new PlayEvent(), 1, 2);
    }

    // Create this class that extends ObjectMapper, because ObjectMapper.writeValueAsString() throws unchecked exception & does not compile
    private class SafeObjectMapper extends ObjectMapper {

        @Override
        public String writeValueAsString(final Object value) {
            return "Hello, World!";
        }
    }
}
