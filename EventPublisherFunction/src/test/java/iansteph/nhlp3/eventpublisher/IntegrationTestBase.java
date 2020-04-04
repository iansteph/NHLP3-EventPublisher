package iansteph.nhlp3.eventpublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class IntegrationTestBase {

    protected static RestTemplate createRestTemplateAndRegisterCustomObjectMapper() {

        final RestTemplate restTemplate = new RestTemplate();
        final MappingJackson2HttpMessageConverter messageConverter = restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst().orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"));
        messageConverter.getObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                false);
        return restTemplate;
    }

    protected static ObjectMapper getObjectMapperFromRestTemplate(final RestTemplate restTemplate) {

        return restTemplate.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst().orElseThrow( () -> new RuntimeException("MappingJackson2HttpMessageConverter not found"))
                .getObjectMapper();
    }

    public static class SqsQueueMetadata {

        private final String queueUrl;
        private String queueArn;

        public SqsQueueMetadata(final String queueUrl) {

            this.queueUrl = queueUrl;
        }

        public String getQueueUrl() {

            return this.queueUrl;
        }

        public void setQueueArn(final String queueArn) {

            this.queueArn = queueArn;
        }

        public String getQueueArn() {

            return this.queueArn;
        }
    }
}
