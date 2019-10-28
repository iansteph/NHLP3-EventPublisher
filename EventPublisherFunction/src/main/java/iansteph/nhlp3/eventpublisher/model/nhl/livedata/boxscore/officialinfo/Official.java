package iansteph.nhlp3.eventpublisher.model.nhl.livedata.boxscore.officialinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Official {

    private int id;
    private String fullName;
    private String link;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Official{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Official official = (Official) o;
        return id == official.id &&
                Objects.equals(fullName, official.fullName) &&
                Objects.equals(link, official.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, link);
    }
}
