package iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.linescore.period.Side;

import java.time.ZonedDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Period {

    private String periodType;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private int num;
    private String ordinalNum;
    private Side home;
    private Side away;

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(final String periodType) {
        this.periodType = periodType;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(final ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(final ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(final int num) {
        this.num = num;
    }

    public String getOrdinalNum() {
        return ordinalNum;
    }

    public void setOrdinalNum(final String ordinalNum) {
        this.ordinalNum = ordinalNum;
    }

    public Side getHome() {
        return home;
    }

    public void setHome(final Side home) {
        this.home = home;
    }

    public Side getAway() {
        return away;
    }

    public void setAway(final Side away) {
        this.away = away;
    }

    @Override
    public String toString() {
        return "Period{" +
                "periodType='" + periodType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", num=" + num +
                ", ordinalNum='" + ordinalNum + '\'' +
                ", home=" + home +
                ", away=" + away +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return num == period.num &&
                Objects.equals(periodType, period.periodType) &&
                Objects.equals(startTime, period.startTime) &&
                Objects.equals(endTime, period.endTime) &&
                Objects.equals(ordinalNum, period.ordinalNum) &&
                Objects.equals(home, period.home) &&
                Objects.equals(away, period.away);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodType, startTime, endTime, num, ordinalNum, home, away);
    }
}
