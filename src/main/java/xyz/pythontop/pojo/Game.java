package xyz.pythontop.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "startDate")
    private LocalDateTime startDate;

    @JsonProperty(value = "endDate")
    private LocalDateTime endDate;

    @JsonProperty(value = "status")
    private String status;

    @JsonProperty(value = "url")
    private String url;
}
