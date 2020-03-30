package iansteph.nhlp3.eventpublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

public class AppConfig {

    private static final String APP_CONFIG_FILE_NAME = "application-configuration.json";
    private static final Logger logger = LogManager.getLogger(AppConfig.class);

    private AppConfig() {}

    public static JsonNode initialize() {

        final String stage = System.getenv("Stage");
        return initialize(stage);
    }

    public static JsonNode initialize(final String stage) {

        final ObjectMapper objectMapper = new ObjectMapper();
        try {

            final String appConfigFilePath = AppConfig.class.getClassLoader().getResource(APP_CONFIG_FILE_NAME).getFile();
            final File appConfigFile = new File(appConfigFilePath);
            final JsonNode appConfig = objectMapper.readTree(appConfigFile);
            logger.info(format("Initialized app config for stage: %s", stage));
            return appConfig.get(stage);
        }
        catch (JsonProcessingException e) {

            logger.error(format("Encountered exception parsing JSON in app config file. Exception: %s", e.getMessage()), e);
            throw new RuntimeException(e);
        }
        catch (IOException e) {

            logger.error(format("Encountered exception reading app config file. Exception: %s", e.getMessage()), e);
            throw new RuntimeException(e);
        }
    }
}
