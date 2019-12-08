package iansteph.nhlp3.eventpublisher.proxy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.eventpublisher.UnitTestBase;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.Play;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.Result;
import org.junit.Test;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventPublisherProxyTest extends UnitTestBase {

    private final SnsClient mockAmazonSnsClient = mock(SnsClient.class);
    private final SafeObjectMapper mockObjectMapper = mock(SafeObjectMapper.class);
    private final EventPublisherProxy eventPublisherProxy = new EventPublisherProxy(mockAmazonSnsClient, mockObjectMapper, "someTopicArn");

    @Test
    public void testPublishIsSuccessful() {

        final Play play = new Play();
        final Result result = new Result();
        result.setEventTypeId("Game_End");
        play.setResult(result);
        final PlayEvent playEvent = new PlayEvent().withPlay(play).withGamePk(GameId);
        when(mockAmazonSnsClient.publish(any(PublishRequest.class))).thenReturn(PublishResponse.builder().build());
        when(mockObjectMapper.writeValueAsString(playEvent)).thenReturn(playEvent.toString());

        eventPublisherProxy.publish(playEvent, 1, 2);

        verify(mockAmazonSnsClient, times(1)).publish(any(PublishRequest.class));
    }

    @Test(expected = RuntimeException.class)
    public void testPublishThrowsJsonProcessingExceptionIfDeserializingResponseToStringFails() {

        when(mockObjectMapper.writeValueAsString(any(PlayEvent.class))).thenThrow(new JsonParseException(null, ""));

        eventPublisherProxy.publish(new PlayEvent(), 1, 2);
    }

    @Test(expected = RuntimeException.class)
    public void testPublishCatchesJsonProcessingExceptionFromObjectMapperAndThrowsRuntimeException() {

        when(mockAmazonSnsClient.publish(any(PublishRequest.class))).thenReturn(PublishResponse.builder().build());

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
