package iansteph.nhlp3.eventpublisher;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AppConfigTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @After
    public void cleanUp() {
        environmentVariables.clear("Stage");
    }

    @Test
    public void test_initialize_successfully_initializes_app_config() {
        environmentVariables.set("Stage", "personal");

        final JsonNode appConfig = AppConfig.initialize();
        final String result = appConfig.get("integration-tests").get("nhlPlayByPlayEventsTopic").asText();

        assertThat(result, is(notNullValue()));
        assertThat(result, is("NHLP3-Play-by-Play-Events-personal"));
    }

    @Test
    public void test_initialize_successfully_scopes_app_config_to_a_specific_stage() {
        environmentVariables.set("Stage", "prod");

        final JsonNode appConfig = AppConfig.initialize();
        final JsonNode result = appConfig.get("integration-tests");

        assertThat(result, is(nullValue()));
    }
}
