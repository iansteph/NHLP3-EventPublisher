package iansteph.nhlp3.eventpublisher.proxy;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;

import java.util.HashMap;
import java.util.Map;

public class EventPublisherProxy {

    private final AmazonSNS amazonSnsClient;
    private final ObjectMapper objectMapper;
    private final String nhlPlayByPlayEventsTopicArn = "arn:aws:sns:us-east-1:627812672245:NHLP3-Play-by-Play-Events-Prod";

    public EventPublisherProxy(final AmazonSNS amazonSnsClient, final ObjectMapper objectMapper) {

        this.amazonSnsClient = amazonSnsClient;
        this.objectMapper = objectMapper;
    }

    public PublishResult publish(final PlayEvent playEventToPublish, final int homeTeamId, final int awayTeamId) {

        final PublishRequest request = new PublishRequest(nhlPlayByPlayEventsTopicArn, convertPlayEventToString(playEventToPublish))
                .withMessageAttributes(retrieveMessageAttributes(playEventToPublish, homeTeamId, awayTeamId));
        return amazonSnsClient.publish(request);
    }

    private String convertPlayEventToString(final PlayEvent playEvent) {

        try {

            return objectMapper.writeValueAsString(playEvent);
        }
        catch (JsonProcessingException e) {

            throw new RuntimeException(String.format("GameId %s | Exception was thrown: %s with cause %s", playEvent.getGamePk(),
                    e.getMessage(), e.getCause()));
        }
    }

    private Map<String, MessageAttributeValue> retrieveMessageAttributes(
            final PlayEvent playEvent,
            final int homeTeamId,
            final int awayTeamId
    ) {

        final String numberDataType = "Number";
        final Map<String, MessageAttributeValue> messageAttributeValueMap = new HashMap<>();
        messageAttributeValueMap.put("eventTypeId", new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(playEvent.getPlay().getResult().getEventTypeId()));
        messageAttributeValueMap.put("homeTeamId", new MessageAttributeValue()
                .withDataType(numberDataType)
                .withStringValue(String.valueOf(homeTeamId)));
        messageAttributeValueMap.put("awayTeamId", new MessageAttributeValue()
                .withDataType(numberDataType)
                .withStringValue(String.valueOf(awayTeamId)));
        return messageAttributeValueMap;
    }
}
