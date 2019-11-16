package iansteph.nhlp3.eventpublisher.proxy;

import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.handler.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NhlPlayByPlayProxy {

    private final NhlPlayByPlayClient nhlPlayByPlayClient;

    private static final Logger logger = LogManager.getLogger(NhlPlayByPlayProxy.class);

    public NhlPlayByPlayProxy(final NhlPlayByPlayClient nhlPlayByPlayClient) {
        this.nhlPlayByPlayClient = nhlPlayByPlayClient;
    }

    public String getPlayByPlayEventsSinceLastProcessedTimestamp(final String lastProcessedTimestamp,
            final EventPublisherRequest eventPublisherRequest) {
        try {
            validateArguments(lastProcessedTimestamp, eventPublisherRequest);
            return nhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(eventPublisherRequest.getGameId(),
                    lastProcessedTimestamp);
        }
        catch (NullPointerException | IllegalArgumentException e) {
            logger.error(e);
            throw e;
        }
    }

    private void validateArguments(final String lastProcessedTimestamp, final EventPublisherRequest eventPublisherRequest) {
        checkNotNull(eventPublisherRequest, "Invalid Argument: EventPublisherRequest must be non-null when calling" +
                "NhlPlayByPlayProxy::getPlayByPlayEventsSinceLastProcessedTimestamp");
        validateLastProcessedTimestamp(lastProcessedTimestamp);
    }

    private void validateLastProcessedTimestamp(final String lastProcessedTimestamp) {
        // According to https://gitlab.com/dword4/nhlapi/blob/master/stats-api.md#game it is of the format yyyymmdd_hhmmss
        checkNotNull(lastProcessedTimestamp, "Invalid Argument: lastProcessedTimestamp must be non-null when calling" +
                "NhlPlayByPlayProxy::getPlayByPlayEventsSinceLastProcessedTimestamp");
        checkArgument(lastProcessedTimestamp.length() == 15, "Invalid Argument: LastProcessedTimestamp must match" +
                "pattern for the NHL Play-by-Play API");

        final String date = lastProcessedTimestamp.substring(0, 8);
        LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);

        final String time = String.format("%s:%s:%s", lastProcessedTimestamp.substring(9, 11), lastProcessedTimestamp.substring(11, 13),
                lastProcessedTimestamp.substring(13));
        LocalTime.parse(time);
    }
}
