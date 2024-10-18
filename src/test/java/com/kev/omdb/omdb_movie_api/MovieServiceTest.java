package com.kev.omdb.omdb_movie_api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kev.omdb.omdb_movie_api.api.OmdbApi;
import com.kev.omdb.omdb_movie_api.model.MovieInfo;
import com.kev.omdb.omdb_movie_api.model.MovieResult;
import com.kev.omdb.omdb_movie_api.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class MovieServiceTest {

    @Mock
    ObjectMapper objectMapper;

    @Mock
    private OmdbApi omdbApi;

    @Mock
    private List<MovieInfo> favoritesList;

    private MovieService movieService;
    private MovieService movieServiceSpy;
    private MovieInfo movieInfo;
    private MovieInfo movieInfo2;
    private MovieResult movResult;

    private Map<String, Object> movieResult;
    private List<MovieInfo> searchResults;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        favoritesList = new ArrayList<MovieInfo>();

        movieService = new MovieService(omdbApi,objectMapper,favoritesList);
        movieServiceSpy = Mockito.spy(movieService);

        movieInfo = new MovieInfo();
        movieInfo.setTitle("Saw");
        movieInfo.setGenre("Horror");
        movieInfo.setImdbId("tt67890");
        movieInfo.setLanguage("English");
        movieInfo.setYear("1999");
        movieInfo.setPlot("A horror movie");

        movResult = new MovieResult();
        movResult.setData(movieInfo);
        movResult.setStatusCode(HttpStatus.OK);

        movieInfo2 = new MovieInfo();
        movieInfo2.setTitle("Lion King");
        movieInfo2.setGenre("Cartoon");
        movieInfo2.setImdbId("tt1234");
        movieInfo2.setLanguage("English");
        movieInfo2.setYear("1993");
        movieInfo2.setPlot("A cartoon movie");



        movieResult = Map.ofEntries(
                Map.entry("Title", "Toy Story"),
                Map.entry("imdbId", "tt67890"),
                Map.entry("Language", "French"),
                Map.entry("Genre", "Family"),
                Map.entry("Year","2001"),
                Map.entry("Plot", "A cartoon about toys"),
                Map.entry("Response", "True")
        );

        searchResults = new ArrayList<>();

    }

    //test getMovieInfoById Success
    @Test
    void testGetMovieInfoByIdSuccess() {
        Mockito.when(omdbApi.getMovieInfoById("4387","tt67890")).thenReturn(movieResult);
        when(objectMapper.convertValue(movieResult,MovieInfo.class)).thenReturn(movieInfo);

        MovieInfo result = movieService.getMovieInfoById("4387","tt67890").getData();

        assertEquals(movieInfo.getTitle(),result.getTitle());

    }

    //test getMovieInfoById Failure
    @Test
    void testGetMovieInfoByIdFailure() {
        Map<String, Object> resultCopy = new HashMap<>(movieResult);
        resultCopy.replace("Response","False");

        Mockito.when(omdbApi.getMovieInfoById("4387","tt67890")).thenReturn(resultCopy);
        when(objectMapper.convertValue(movieResult,MovieInfo.class)).thenReturn(movieInfo);

        MovieInfo result = movieService.getMovieInfoById("4387","tt67890").getData();

        assertNull(result);
    }

    //Test successful getFavoritesList call
    @Test
    void getFavoritesList() {
        Mockito.when(omdbApi.getMovieInfoById("4387","tt67890")).thenReturn(movieResult);
        when(movieServiceSpy.getMovieInfoById("4387","tt67890")).thenReturn(movResult);
        when(objectMapper.convertValue(movieResult,MovieInfo.class)).thenReturn(movieInfo);

        movieService.addFavorite("4387","tt67890");

        assertEquals(1,movieService.getFavoritesList().size());

    }

    //Test to validate Add Favorite Success
    @Test
    void addFavoriteSuccess() {
        Mockito.when(omdbApi.getMovieInfoById("4387","tt67890")).thenReturn(movieResult);
        when(movieServiceSpy.getMovieInfoById("4387","tt67890")).thenReturn(movResult);
        when(objectMapper.convertValue(movieResult,MovieInfo.class)).thenReturn(movieInfo);

        MovieResult movieResultCopy = movieService.addFavorite("4387","tt67890");

        assertEquals(HttpStatus.OK,movieResultCopy.getStatusCode());
    }

    //Test to validate Add Favorite Failure
    @Test
    void addFavoriteFailure() {
       MovieResult movResult2 = new MovieResult();
       movResult2.setStatusCode(HttpStatus.BAD_REQUEST);

        when(omdbApi.getMovieInfoById("4387","tt57890")).thenReturn(null);
        when(objectMapper.convertValue(movieResult,MovieInfo.class)).thenReturn(movieInfo);

        System.out.println(movResult);
        MovieResult movieResultCopy = movieService.addFavorite("4387","tt57890");
        System.out.println("value of add:" + movieResultCopy);

        assertEquals(HttpStatus.BAD_REQUEST,movieResultCopy.getStatusCode());
    }

    //Test to confirm removeFavorites removes item from list
    @Test
    void testRemoveFavoritesListSize() {
        favoritesList.add(movieInfo);
        favoritesList.add(movieInfo2);

        movieService.removeFavorite("tt1234");

        assertEquals(1,movieService.getFavoritesList().size());
    }

    //Test for empty list, return false result
    @Test
    void testRemoveFavoritesListSuccess() {
        favoritesList.add(movieInfo2);
        MovieResult movieResult2 = movieService.removeFavorite("tt1234");

        assertEquals(HttpStatus.OK,movieResult2.getStatusCode());
    }

    //Test for null imdb id
    @Test
    void testRemoveFavoritesListNullImdbId()  {
        movieInfo2.setImdbId(null);
        favoritesList.add(movieInfo2);

        MovieResult movieResult2 = movieService.removeFavorite("tt1234");

        assertEquals(HttpStatus.BAD_REQUEST,movieResult2.getStatusCode());
    }


}
