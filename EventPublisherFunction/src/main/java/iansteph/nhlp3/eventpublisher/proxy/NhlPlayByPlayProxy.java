package iansteph.nhlp3.eventpublisher.proxy;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class NhlPlayByPlayProxy {

    private final NhlPlayByPlayClient nhlPlayByPlayClient;
    private final AmazonS3 amazonS3Client;
    private final String nhlPlayByPlayResponseArchiveS3BucketName;

    private static final Logger logger = LogManager.getLogger(NhlPlayByPlayProxy.class);

    public NhlPlayByPlayProxy(
            final NhlPlayByPlayClient nhlPlayByPlayClient,
            final AmazonS3 amazonS3Client,
            final String nhlPlayByPlayResponseArchiveS3BucketName
    ) {

        this.nhlPlayByPlayClient = nhlPlayByPlayClient;
        this.amazonS3Client = amazonS3Client;
        this.nhlPlayByPlayResponseArchiveS3BucketName = nhlPlayByPlayResponseArchiveS3BucketName;
    }

    public Optional<NhlLiveGameFeedResponse> getPlayByPlayEventsSinceLastProcessedTimestamp(
            final String lastProcessedTimestamp,
            final EventPublisherRequest eventPublisherRequest
    ) {

        final int gameId = eventPublisherRequest.getGameId();
        try {

            validateArguments(gameId, lastProcessedTimestamp, eventPublisherRequest);
            final String response =  nhlPlayByPlayClient.getPlayByPlayEventsSinceLastProcessedTimestamp(gameId,
                    lastProcessedTimestamp);
            archiveResponseToS3(response, gameId, lastProcessedTimestamp);
            return nhlPlayByPlayClient.deserializeResponse(response, gameId, lastProcessedTimestamp);
        }
        catch (NullPointerException | IllegalArgumentException e) {

            logger.error(e);
            throw e;
        }
    }

    private void validateArguments(
            final int gameId,
            final String lastProcessedTimestamp,
            final EventPublisherRequest eventPublisherRequest
    ) {

        checkNotNull(eventPublisherRequest, format("GameId %s | EventPublisherRequest must be non-null when calling" +
                "NhlPlayByPlayProxy::getPlayByPlayEventsSinceLastProcessedTimestamp at lastProcessedTimestamp %s", gameId,
                lastProcessedTimestamp));
        validateLastProcessedTimestamp(gameId, lastProcessedTimestamp);
    }

    private void validateLastProcessedTimestamp(final int gameId, final String lastProcessedTimestamp) {

        // According to https://gitlab.com/dword4/nhlapi/blob/master/stats-api.md#game it is of the format yyyymmdd_hhmmss
        checkNotNull(lastProcessedTimestamp, format("GameId %s | lastProcessedTimestamp must be non-null when calling" +
                "NhlPlayByPlayProxy::getPlayByPlayEventsSinceLastProcessedTimestamp at lastProcessedTimestamp %s", gameId,
                lastProcessedTimestamp));
        checkArgument(lastProcessedTimestamp.length() == 15, format("GameId %s | LastProcessedTimestamp must match " +
                "pattern for the NHL Play-by-Play API at lastProcessedTimestamp %s", gameId, lastProcessedTimestamp));
        final String date = lastProcessedTimestamp.substring(0, 8);
        LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
        final String time = String.format("%s:%s:%s", lastProcessedTimestamp.substring(9, 11), lastProcessedTimestamp.substring(11, 13),
                lastProcessedTimestamp.substring(13));
        LocalTime.parse(time);
    }

    private void archiveResponseToS3(final String response, final int gameId, final String lastProcessedTimestamp) {

        final String s3ObjectKey = createS3ObjectKey(String.valueOf(gameId), lastProcessedTimestamp);
        try {

            amazonS3Client.putObject(nhlPlayByPlayResponseArchiveS3BucketName, s3ObjectKey, response);
            logger.info(format("GameId %s | Archived NHL Play-by-Play API response at lastProcessedTimestamp %s to S3", gameId,
                    lastProcessedTimestamp));
        } catch (AmazonServiceException e) {

            logger.error(format("GameId %s | Encountered a server-side exception when attempting to archive the NHL Play-by-Play API " +
                    "response to S3. S3 couldn't successfully handle the request. Exception: %s", gameId, e.getMessage()), e);
        } catch (SdkClientException e) {

            logger.error(format("GameId %s | Encountered a client-side exception when attempting to archive the NHL Play-by-Play API " +
                    "response to S3. Either S3 couldn't be contacted by the client or the client couldn't parse the response from S3. " +
                    "Exception: %s", gameId, e.getMessage()), e);
        }
    }

    // Creates an S3 object key of the format "seasonStartYear-seasonEndYear/gameId/gameId-lastProcessedTimestamp" within the S3 bucket
    // Example: "2019-2020/2019020054/2019020054-20191122_173415" for gameId 2019020054
    private String createS3ObjectKey(final String gameId, final String lastProcessedTimestamp) {

        // Sample GameId: 2019020054
        //
        // Verified on nhl.com that when the calendar year changes for a season the gameId prefix of the year does not change (example:
        // gameId 2018021229 was in the 2018-2019 season but played in calendar year 2019 yet the gameId started with "2018")
        final int seasonStartYear = Integer.parseInt(gameId.substring(0, 4));
        final int seasonEndYear = seasonStartYear + 1;
        final String season = format("%s-%s", seasonStartYear, seasonEndYear);
        final String objectName = format("%s-%s", gameId, lastProcessedTimestamp);
        return format("%s/%s/%s", season, gameId, objectName);
    }
}
