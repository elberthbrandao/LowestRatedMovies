package com.elberthbrandao.LowestRatedMovies.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elberthbrandao.LowestRatedMovies.entities.Movie;
import com.elberthbrandao.LowestRatedMovies.service.MovieService;

@RestController
@RequestMapping(value = "/movies")
public class MovieController {
	
	@Autowired
	private MovieService service;
	
	@GetMapping(value = "lowest-rated")
	public ResponseEntity<List<Movie>> findTopLowestRatedMovies() {
		List<Movie> movieList = service.findTopLowestRatedMovies();
		return ResponseEntity.ok(movieList);
	}

}
