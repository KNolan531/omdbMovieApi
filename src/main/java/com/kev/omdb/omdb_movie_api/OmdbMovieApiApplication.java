package com.kev.omdb.omdb_movie_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OmdbMovieApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmdbMovieApiApplication.class, args);
	}

}
