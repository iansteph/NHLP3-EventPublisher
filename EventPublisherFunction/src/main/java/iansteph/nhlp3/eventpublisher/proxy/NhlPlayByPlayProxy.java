package iansteph.nhlp3.eventpublisher.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iansteph.nhlp3.eventpublisher.client.NhlPlayByPlayClient;
import iansteph.nhlp3.eventpublisher.model.request.EventPublisherRequest;
import iansteph.nhlp3.eventpublisher.model.nhl.NhlLiveGameFeedResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class NhlPlayByPlayProxy {

    private final NhlPlayByPlayClient nhlPlayByPlayClient;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final String nhlPlayByPlayResponseArchiveS3BucketName;

    private static final Logger logger = LogManager.getLogger(NhlPlayByPlayProxy.class);

    public NhlPlayByPlayProxy(
            final NhlPlayByPlayClient nhlPlayByPlayClient,
            final ObjectMapper objectMapper,
            final S3Client s3Client,
            final String nhlPlayByPlayResponseArchiveS3BucketName
    ) {

        this.nhlPlayByPlayClient = nhlPlayByPlayClient;
        this.objectMapper = objectMapper;
        this.s3Client = s3Client;
        this.nhlPlayByPlayResponseArchiveS3BucketName = nhlPlayByPlayResponseArchiveS3BucketName;
    }

    public Optional<NhlLiveGameFeedResponse> getPlayByPlayData(final EventPublisherRequest eventPublisherRequest) {

        try {

            validateArguments(eventPublisherRequest);
            final int gameId = eventPublisherRequest.getGameId();
            final NhlLiveGameFeedResponse response = nhlPlayByPlayClient.getPlayByPlayData(gameId);
            archiveResponseToS3(response, gameId);
            return Optional.ofNullable(response);
        }
        catch (NullPointerException e) {

            logger.error(e);
            throw e;
        }
    }

    private void validateArguments(final EventPublisherRequest eventPublisherRequest) {

        checkNotNull(eventPublisherRequest, "EventPublisherRequest must be non-null when calling" +
                "NhlPlayByPlayProxy::getPlayByPlayData");
    }

    private void archiveResponseToS3(final NhlLiveGameFeedResponse response, final int gameId) {

        final String nowTimestamp = createS3FormattedTimestamp();
        final String s3ObjectKey = createS3ObjectKey(String.valueOf(gameId), nowTimestamp);
        try {

            final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(nhlPlayByPlayResponseArchiveS3BucketName)
                    .key(s3ObjectKey)
                    .build();
            final String serializedResponse = objectMapper.writeValueAsString(response);
            final RequestBody requestBody = RequestBody.fromString(serializedResponse);
            s3Client.putObject(putObjectRequest, requestBody);
            logger.info(format("GameId %s | Archived NHL Play-by-Play API response at timestamp %s to S3", gameId,
                    nowTimestamp));
        } catch (JsonProcessingException | SdkException e) {

            logger.error(format("GameId %s | Encountered a exception when attempting to archive the NHL Play-by-Play API " +
                    "response to S3. Exception: %s", gameId, e.getMessage()), e);
        }
    }

    private String createS3FormattedTimestamp() {

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss--z");
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return dateTimeFormatter.format(now);
    }

    // Creates an S3 object key of the format "seasonStartYear-seasonEndYear/gameId/gameId-lastProcessedTimestamp" within the S3 bucket
    // Example: "2019-2020/2019020054/2019020054-20191122_173415" for gameId 2019020054
    private String createS3ObjectKey(final String gameId, final String nowTimestamp) {

        // Sample GameId: 2019020054
        //
        // Verified on nhl.com that when the calendar year changes for a season the gameId prefix of the year does not change (example:
        // gameId 2018021229 was in the 2018-2019 season but played in calendar year 2019 yet the gameId started with "2018")
        final int seasonStartYear = Integer.parseInt(gameId.substring(0, 4));
        final int seasonEndYear = seasonStartYear + 1;
        final String season = format("%s-%s", seasonStartYear, seasonEndYear);
        final String objectName = format("%s_%s", gameId, nowTimestamp);
        return format("%s/%s/%s", season, gameId, objectName);
    }
}
