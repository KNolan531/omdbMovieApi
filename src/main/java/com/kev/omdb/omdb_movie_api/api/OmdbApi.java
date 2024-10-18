/**
 * OmdbApi.java
 *
 * A FeignClient interface class, takes advantage of Spring feign library to more efficiently call the 2 external
 * api endpoints from omdb website. These methods are then called from other Service classes as needed.
 */

package com.kev.omdb.omdb_movie_api.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@FeignClient(name ="omdb", url ="${omdb.url}")
public interface OmdbApi {

    @GetMapping
    Map<String, Object> getMovieInfoById(@RequestParam(value="apikey") String apiKey,@RequestParam( value = "i") String imdbId);

    @GetMapping
    Map<String,Object> searchMovies(@RequestParam(value="apikey") String apiKey,@RequestParam( value = "s") String searchValue);

}
