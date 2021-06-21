package xyz.pythontop.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "effectiveDate")
    private LocalDateTime effectiveDate;

    @JsonProperty(value = "urlSlug")
    private String urlSlug;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getUrlSlug() {
        return urlSlug;
    }

    public void setUrlSlug(String urlSlug) {
        this.urlSlug = urlSlug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return title.equals(game.title) && effectiveDate.equals(game.effectiveDate) && urlSlug.equals(game.urlSlug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, effectiveDate, urlSlug);
    }

    @Override
    public String toString() {
        return "Game{" +
                "title='" + title + '\'' +
                ", effectiveDate=" + effectiveDate +
                ", urlSlug='" + urlSlug + '\'' +
                '}';
    }
}
