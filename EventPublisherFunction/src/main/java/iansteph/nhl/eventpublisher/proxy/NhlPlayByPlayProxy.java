package iansteph.nhl.eventpublisher.proxy;

import iansteph.nhl.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhl.eventpublisher.handler.EventPublisherRequest;
import iansteph.nhl.eventpublisher.model.nhl.NhlLiveGameFeedResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NhlPlayByPlayProxy {

    private final NhlPlayByPlayClient nhlPlayByPlayClient;

    public NhlPlayByPlayProxy(final NhlPlayByPlayClient nhlPlayByPlayClient) {
        this.nhlPlayByPlayClient = nhlPlayByPlayClient;
    }

    public NhlLiveGameFeedResponse getPlayByPlayEventsSinceLastProcessedTimestamp(final String lastProcessedTimestamp,
            final EventPublisherRequest eventPublisherRequest) {
        validateArguments(lastProcessedTimestamp, eventPublisherRequest);
        return nhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(eventPublisherRequest.getGameId(),
                lastProcessedTimestamp);
    }

    private void validateArguments(final String lastProcessedTimestamp, final EventPublisherRequest eventPublisherRequest) {
        checkNotNull(eventPublisherRequest);
        validateLastProcessedTimestamp(lastProcessedTimestamp);
    }

    private void validateLastProcessedTimestamp(final String lastProcessedTimestamp) {
        // According to https://gitlab.com/dword4/nhlapi/blob/master/stats-api.md#game it is of the format yyyymmdd_hhmmss
        checkNotNull(lastProcessedTimestamp);
        checkArgument(lastProcessedTimestamp.length() == 15);

        final String date = lastProcessedTimestamp.substring(0, 8);
        LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);

        final String time = String.format("%s:%s:%s", lastProcessedTimestamp.substring(9, 11), lastProcessedTimestamp.substring(11, 13),
                lastProcessedTimestamp.substring(13));
        LocalTime.parse(time);
    }
}
