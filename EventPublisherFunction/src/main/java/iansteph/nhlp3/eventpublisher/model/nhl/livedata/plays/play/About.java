package iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.plays.play.about.Goals;

import java.time.ZonedDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class About {

    private int eventIdx;
    private int eventId;
    private int period;
    private String periodType;
    private String ordinalNum;
    private String periodTime;
    private String periodTimeRemaining;
    private ZonedDateTime dateTime;
    private Goals goals;

    public int getEventIdx() {
        return eventIdx;
    }

    public void setEventIdx(final int eventIdx) {
        this.eventIdx = eventIdx;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(final int eventId) {
        this.eventId = eventId;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(final int period) {
        this.period = period;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(final String periodType) {
        this.periodType = periodType;
    }

    public String getOrdinalNum() {
        return ordinalNum;
    }

    public void setOrdinalNum(final String ordinalNum) {
        this.ordinalNum = ordinalNum;
    }

    public String getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(final String periodTime) {
        this.periodTime = periodTime;
    }

    public String getPeriodTimeRemaining() {
        return periodTimeRemaining;
    }

    public void setPeriodTimeRemaining(final String periodTimeRemaining) {
        this.periodTimeRemaining = periodTimeRemaining;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(final ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Goals getGoals() {
        return goals;
    }

    public void setGoals(final Goals goals) {
        this.goals = goals;
    }

    @Override
    public String toString() {
        return "About{" +
                "eventIdx=" + eventIdx +
                ", eventId=" + eventId +
                ", period=" + period +
                ", periodType='" + periodType + '\'' +
                ", ordinalNum='" + ordinalNum + '\'' +
                ", periodTime='" + periodTime + '\'' +
                ", periodTimeRemaining='" + periodTimeRemaining + '\'' +
                ", dateTime=" + dateTime +
                ", goals=" + goals +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        About about = (About) o;
        return eventIdx == about.eventIdx &&
                eventId == about.eventId &&
                period == about.period &&
                Objects.equals(periodType, about.periodType) &&
                Objects.equals(ordinalNum, about.ordinalNum) &&
                Objects.equals(periodTime, about.periodTime) &&
                Objects.equals(periodTimeRemaining, about.periodTimeRemaining) &&
                Objects.equals(dateTime, about.dateTime) &&
                Objects.equals(goals, about.goals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventIdx, eventId, period, periodType, ordinalNum, periodTime, periodTimeRemaining, dateTime, goals);
    }
}
