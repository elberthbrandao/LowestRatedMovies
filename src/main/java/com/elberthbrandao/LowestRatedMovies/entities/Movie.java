package com.elberthbrandao.LowestRatedMovies.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie implements Comparable<Movie>{

	private String title;
	private String originalTitle;
	private Double rating;
	private List<String> directors;
	private List<String> topCast;
	private String comment;

	@Override
	public int compareTo(Movie movie) {
		if (this.rating < movie.getRating()) {
			return +1;
		} else if(this.rating > movie.getRating()){
			return -1;
		} 
		return 0;
	}
	
}
