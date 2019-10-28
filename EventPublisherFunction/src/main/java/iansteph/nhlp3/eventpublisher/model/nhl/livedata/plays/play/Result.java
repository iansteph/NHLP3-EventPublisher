package iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    private String event;
    private String eventCode;
    private String eventTypeId;
    private String description;

    public String getEvent() {
        return event;
    }

    public void setEvent(final String event) {
        this.event = event;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(final String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(final String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Result{" +
                "event='" + event + '\'' +
                ", eventCode='" + eventCode + '\'' +
                ", eventTypeId='" + eventTypeId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return Objects.equals(event, result.event) &&
                Objects.equals(eventCode, result.eventCode) &&
                Objects.equals(eventTypeId, result.eventTypeId) &&
                Objects.equals(description, result.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, eventCode, eventTypeId, description);
    }
}
