/**
 * ValidationUtil.java
 *
 * A simple helper class designed to validate the data being passed through the
 *  various methods in the Service class. In most cases it updates the MovieResult object being
 *  passed through with the appropriate data and status response.
 */

package com.kev.omdb.omdb_movie_api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev.omdb.omdb_movie_api.model.MovieInfo;
import com.kev.omdb.omdb_movie_api.model.MovieResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class ValidationUtil {

    private ObjectMapper mapper = new ObjectMapper();

    /**
     *
     * @param movieResult - MovieResult object to manage the return data,status code,error msg
     * @param objectResult - the Map returned by omdbApi call
     */
    public void validateMovieInfoById(MovieResult movieResult, Map<String, Object>  objectResult) {

        if(objectResult != null) {
            if(objectResult.containsValue("True") ){
                movieResult.setData(mapper.convertValue(objectResult, MovieInfo.class));
                movieResult.setStatusCode(HttpStatus.OK);
            }
            else{
                System.out.println(objectResult.get("Error"));
                movieResult.setErrorMsg( objectResult.get("Error"));
                movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
            }
        }
        else {
            movieResult.setErrorMsg( "Null value returned");
            movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *
     * @param movieResult - MovieResult object to manage the return data,status code,error msg
     * @param favorites - the list of Favorites to validate data for
     * @param imdbId - the imdb id used to search for id
     */
    public void validateRemoveFavorite(MovieResult movieResult,List<MovieInfo> favorites,String imdbId) {
        try {

            for(MovieInfo mov: favorites) {
                if(mov.getImdbId().equals(imdbId)){
                    movieResult.setData(mov);
                    movieResult.setStatusCode(HttpStatus.OK);
                }
            }

            if(movieResult.getStatusCode() == null)
            {
                movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
                movieResult.setErrorMsg("Unable to find IMDB ID in List");
            }

        }
        catch (NullPointerException e) {
            movieResult.setErrorMsg(e.getMessage());
            movieResult.setStatusCode(HttpStatus.BAD_REQUEST);
            System.out.println(e.getMessage());
        }

    }

    /**
     *
     * @param imdbId - the id to validate, it must start with tt and contain only # and letters
     * @return - false if value conditions aren't met,  true otherwise.
     */
    public Boolean validateImdbId(String imdbId) {
        if (!imdbId.contains("tt") || !imdbId.matches("^[a-zA-Z0-9]+$"))
            return false;

        return true;
    }


}
