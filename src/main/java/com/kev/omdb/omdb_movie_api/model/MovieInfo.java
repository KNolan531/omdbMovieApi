/**
 * MovieInfo.java
 *
 * A Model class that sets the values for each movie returned from OMDB api calls that can be returned to a user.
 *
 */

package com.kev.omdb.omdb_movie_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true )
public class MovieInfo {

    @JsonProperty("imdbID")
    private String imdbId;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("Plot")
    private String plot;


}
