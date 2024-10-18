/**
 * MovieController.java
 *
 * A Controller class that initializes endpoints that a user can call to get data results about movies from OMDB,
 * ,add movies to a favorites list stored in memory, remove a movie or return the favorites list.
 *
 */

package com.kev.omdb.omdb_movie_api.controller;

import com.kev.omdb.omdb_movie_api.model.MovieInfo;
import com.kev.omdb.omdb_movie_api.model.MovieResult;
import com.kev.omdb.omdb_movie_api.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping( value = "/omdb")
public class MovieController {

    @Autowired
    MovieService movieService;

    @GetMapping("/moviesList")
    public ResponseEntity searchMovies(@RequestParam(value="apikey") String apiKey, @RequestParam( value = "searchValue") String searchValue){
        return movieService.searchMovies(apiKey,searchValue);
    }

    @PostMapping("/favorites/new")
    public ResponseEntity addFavorite(@RequestParam(value="apikey") String apiKey, @RequestParam(value="imdbId") String imdbId) {
        boolean status = false;
        MovieResult movieResult = movieService.addFavorite(apiKey,imdbId);

        if(movieResult.getStatusCode() == HttpStatus.OK)
            status = true;

        return ResponseEntity.status(movieResult.getStatusCode()).body(status ? "Successfully Added to list!" : movieResult.getErrorMsg());
    }


    @GetMapping("/favorites/list")
    public ResponseEntity<List<MovieInfo>> retrieveFavorites () {
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getFavoritesList());
    }

    @DeleteMapping("/favorites/{imdbId}")
    public ResponseEntity<?> removeFavorite(@PathVariable(value="imdbId") String imdbId) {
        boolean status = false;
        MovieResult  movieResult = movieService.removeFavorite(imdbId);

        if(movieResult.getStatusCode() == HttpStatus.OK)
            status = true;

        return ResponseEntity.status(movieResult.getStatusCode()).body(status ? "Successfully Removed from list! \n" + movieResult.getData() : movieResult.getErrorMsg());

    }




}
