package iansteph.nhlp3.eventpublisher.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.eventpublisher.model.event.PlayEvent;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.HashMap;
import java.util.Map;

public class EventPublisherProxy {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final String nhlPlayByPlayEventsTopicArn = "arn:aws:sns:us-east-1:627812672245:NHLP3-Play-by-Play-Events-Prod";

    public EventPublisherProxy(final SnsClient snsClient, final ObjectMapper objectMapper) {

        this.snsClient = snsClient;
        this.objectMapper = objectMapper;
    }

    public PublishResponse publish(final PlayEvent playEventToPublish, final int homeTeamId, final int awayTeamId) {

        final PublishRequest request = PublishRequest.builder()
                .topicArn(nhlPlayByPlayEventsTopicArn)
                .message(convertPlayEventToString(playEventToPublish))
                .messageAttributes(retrieveMessageAttributes(playEventToPublish, homeTeamId, awayTeamId))
                .build();
        return snsClient.publish(request);
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
        messageAttributeValueMap.put("eventTypeId",
                MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(playEvent.getPlay().getResult().getEventTypeId()).build());
        messageAttributeValueMap.put("homeTeamId",
                MessageAttributeValue.builder()
                        .dataType(numberDataType)
                        .stringValue(String.valueOf(homeTeamId))
                        .build());
        messageAttributeValueMap.put("awayTeamId",
                MessageAttributeValue.builder()
                        .dataType(numberDataType)
                        .stringValue(String.valueOf(awayTeamId))
                        .build());
        return messageAttributeValueMap;
    }
}
