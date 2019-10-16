package iansteph.nhl.eventpublisher.proxy;

import iansteph.nhl.eventpublisher.client.NhlPlayByPlayClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NhlPlayByPlayProxy {

    private NhlPlayByPlayClient nhlPlayByPlayClient;

    public NhlPlayByPlayProxy(final NhlPlayByPlayClient nhlPlayByPlayClient) {
        this.nhlPlayByPlayClient = nhlPlayByPlayClient;
    }

    public Object getPlayByPlayEventsSinceLastProcessedTimestamp(final String lastProcessedTimestamp) {
        validateLastProcessedTimestamp(lastProcessedTimestamp);
        return nhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(lastProcessedTimestamp);
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
