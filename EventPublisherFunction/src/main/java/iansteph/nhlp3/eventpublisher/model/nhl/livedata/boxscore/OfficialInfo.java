package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.officialinfo.Official;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficialInfo {

    private Official official;
    private String officialType;

    public Official getOfficial() {
        return official;
    }

    public void setOfficial(final Official official) {
        this.official = official;
    }

    public String getOfficialType() {
        return officialType;
    }

    public void setOfficialType(final String officialType) {
        this.officialType = officialType;
    }

    @Override
    public String toString() {
        return "OfficialInfo{" +
                "official=" + official +
                ", officialType='" + officialType + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OfficialInfo that = (OfficialInfo) o;
        return Objects.equals(official, that.official) &&
                Objects.equals(officialType, that.officialType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(official, officialType);
    }
}
