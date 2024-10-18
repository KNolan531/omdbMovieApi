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
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class MovieService {

    @Autowired
    OmdbApi omdbApi;

    private ObjectMapper mapper = new ObjectMapper();
    private List<MovieInfo> favoritesList = new ArrayList<>();

    /**
     *
     * @param apikey - apiKey used for omdb endpoint
     * @param imdbId - id of movie to get info for
     * @return MoveInfo object that contains details on Movie found, null if nothing found
     */
    public MovieResult getMovieInfoById(String apikey, String imdbId){
        Map<String, Object> ob;
        MovieInfo movie = null;
        MovieResult movieResult = new MovieResult();

        try {
            ob = omdbApi.getMovieInfoById(apikey, imdbId);

            if(ob != null) {
                if(!ob.containsValue("False") ){
                    movieResult.setData(mapper.convertValue(ob,MovieInfo.class));
                    movieResult.setStatusCode(HttpStatus.OK);
                }
                else{
                    System.out.println(ob.get("Error"));
                    movieResult.setErrorMsg( ob.get("Error"));
                    movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
                }
            }
            else {
                System.out.println("inside of null else:");
                movieResult.setErrorMsg( "Null value returned");
                movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
            }
        } catch (FeignException e) {
            e.printStackTrace();
            movieResult.setErrorMsg( e.getMessage());
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
    public ResponseEntity<?> searchMovies (String apiKey, String searchEntry) {
        List<MovieInfo> movieInfoList = new ArrayList<>();

       try {
           Map<String,Object> results = omdbApi.searchMovies(apiKey,searchEntry);
            if(!results.isEmpty() && results.get("Response").equals("True")) {

                ArrayList<Object> resultsList = mapper.convertValue(results.get("Search"),ArrayList.class);

                int resultsSize = 3;

                if(resultsList.size() < 3)
                    resultsSize = resultsList.size();

                for(int i = 0; i < resultsSize; i++) {
                    MovieInfo movie = mapper.convertValue(resultsList.get(i),MovieInfo.class);
                    movieInfoList.add(getMovieInfoById(apiKey,movie.getImdbId()).getData());
                }
            }
            else{
                System.out.println("No results returned from Search entry");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(results.get("Invalid Search Entry"));
            }

        } catch (FeignException | IllegalArgumentException | HttpStatusCodeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        return ResponseEntity.ok(movieInfoList);
    }

    /**
     *
     * @param apiKey - apiKey used for omdb endpoint
     * @param imdbId - movie to add to favorites list
     * @return boolean - true if able to add, false if invalid value
     */
    public MovieResult addFavorite(String apiKey,String imdbId) {
        MovieResult movieResult = getMovieInfoById(apiKey,imdbId);
        MovieInfo newFavorite = movieResult.getData();

        if(newFavorite != null) {
            favoritesList.add(newFavorite);
              }

        return movieResult;
    }

    /**
     *
     * @param imdbId - movie to remove from favorites list
     * @return boolean - true if able to remove, false if not found/null value in imdbId
     */
    public MovieResult removeFavorite(String imdbId) {
        MovieResult movieResult = new MovieResult();

        try {
            for(MovieInfo mov: favoritesList) {
                if(mov.getImdbId().equals(imdbId)){
                    movieResult.setData(mov);
                    movieResult.setStatusCode(HttpStatus.OK);
                    System.out.println(movieResult.getData());
                }
            }
        }
        catch (NullPointerException e) {
            movieResult.setErrorMsg(e.getMessage());
            movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
            System.out.println(e.getMessage());
        }

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
