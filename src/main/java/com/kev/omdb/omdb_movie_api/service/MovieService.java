/**
 * MovieService.java
 * A Service class that implements methods called by controller for the following:
 *
 *  -Retrieving Movie Search Results by search entry by using OmdbApi feign interface methods
 *  -Adding a movie to a favorites list stored in memory by IMDB ID
 *  -Removing a movie from favorites list by IMDB ID
 *  -Retrieving Favorites List
 *
 */

package com.kev.omdb.omdb_movie_api.service;

import com.kev.omdb.omdb_movie_api.api.OmdbApi;
import com.kev.omdb.omdb_movie_api.model.MovieInfo;
import com.kev.omdb.omdb_movie_api.model.MovieResult;
import com.kev.omdb.omdb_movie_api.util.ValidationUtil;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class MovieService {

    @Autowired
    OmdbApi omdbApi;

    @Autowired
    ValidationUtil validationUtil;

    private ObjectMapper mapper = new ObjectMapper();
    private List<MovieInfo> favoritesList = new ArrayList<>();

    /**
     *
     * @param apikey - apiKey used for omdb endpoint
     * @param imdbId - id of movie to get info for
     * @return MovieResult -contains MovieInfo and OK status if Success or contains BAD REQUEST status + error msg otherwise
     */
    public MovieResult getMovieInfoById(String apikey, String imdbId){
        Map<String, Object> resultObject;
        MovieInfo movie = null;
        MovieResult movieResult = new MovieResult();

        if(validationUtil.validateImdbId(imdbId)) {
            try {
                resultObject = omdbApi.getMovieInfoById(apikey, imdbId);
                validationUtil.validateMovieInfoById(movieResult,resultObject);
            }
            catch (FeignException e) {
                e.printStackTrace();
                movieResult.setErrorMsg( e.getMessage());
                movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            movieResult.setErrorMsg( "Invalid IMDB ID Provided");
            movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
        }

        return movieResult;
    }

    /**
     *
     * @param apiKey - apiKey used for omdb endpoint
     * @param searchEntry - name of Movie to search for
     * @return ResponseEntity containing http status and List of Movies found
     */
    public ResponseEntity searchMovies (String apiKey, String searchEntry) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        Map<String,Object> results;

       try {
            results = omdbApi.searchMovies(apiKey,searchEntry);
            if(!results.isEmpty() && results.get("Response").equals("True")) {

                ArrayList<Object> resultsList = mapper.convertValue(results.get("Search"),ArrayList.class);

                for(int i = 0; i < 3; i++) {
                    MovieInfo movie = mapper.convertValue(resultsList.get(i),MovieInfo.class);
                    movieInfoList.add(getMovieInfoById(apiKey,movie.getImdbId()).getData());
                }
            }
            else{
                System.out.println("No results returned from Search entry");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(results.get("Error"));
            }

        } catch (FeignException | IllegalArgumentException  e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(movieInfoList);
    }

    /**
     *
     * @param apiKey - apiKey used for omdb endpoint
     * @param imdbId - movie to add to favorites list
     * @return MovieResult -contains MovieInfo and OK status if Success or contains BAD REQUEST status + error msg otherwise
     */
    public MovieResult addFavorite(String apiKey,String imdbId) {
        MovieResult movieResult = getMovieInfoById(apiKey,imdbId);
        MovieInfo newFavorite = movieResult.getData();

        if(newFavorite != null && !favoritesList.contains(newFavorite)) {
            favoritesList.add(newFavorite);
              }

        return movieResult;
    }

    /**
     *
     * @param imdbId - movie to remove from favorites list
     * @return MovieResult -contains MovieInfo and OK status if Success or contains BAD REQUEST status + error msg otherwise
     */
    public MovieResult removeFavorite(String imdbId) {
        MovieResult movieResult = new MovieResult();

        validationUtil.validateRemoveFavorite(movieResult,favoritesList,imdbId);

        if(movieResult.getStatusCode() == HttpStatus.OK)
            favoritesList.remove(movieResult.getData());

        return movieResult;
    }

    /**
     *
     * @return List of MovieInfo objects from the favorites that had been added already to list
     */
    public List<MovieInfo> getFavoritesList () {
         return favoritesList;
    }



}
