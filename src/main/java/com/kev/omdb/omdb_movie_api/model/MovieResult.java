/**
 * MovieResult.java
 *
 * A model class created to easily send an object back with HTTP response, error msg and MovieInfo data at same time
 */

package com.kev.omdb.omdb_movie_api.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class MovieResult {

    private HttpStatus statusCode;

    private MovieInfo data;

    private Object errorMsg;


}
