package com.elberthbrandao.LowestRatedMovies.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.elberthbrandao.LowestRatedMovies.entities.Movie;

@Service
public class MovieService {

	@Value("${url.base}")
	private String urlBase;

	@Value("${url.lowest.rated.movies}")
	private String urlLowestRatedMovies;

	@Value("${url.review.by.stars.filter}")
	private String urlReviewByStars;

	@Value("${top.number.list.size}")
	private Integer topNumberListSize;

	public List<Movie> findTopLowestRatedMovies() {
		List<Movie> movies = new ArrayList<>();

		try {
			Document document = Jsoup.connect(urlLowestRatedMovies).get();
			Elements topLowestRatedElements = document.getElementsByClass("lister-list").select("tr");

			for (int i = 0; i < topNumberListSize; i++) {
				Movie movie = new Movie();

				Element movieElement = topLowestRatedElements.get(i);
				String urlMovie = movieElement.getElementsByClass("posterColumn").select("a").attr("href").toString();
				Document pageMovie = Jsoup.connect(urlBase + urlMovie).get();

				setTitle(pageMovie, movie);
				setOriginalTitle(pageMovie, movie);
				setRating(pageMovie, movie);
				setTopCast(pageMovie, movie);
				setDirectorsList(pageMovie, movie);
				setCommentWithFiveStarsOrMore(pageMovie, movie);

				movies.add(movie);
			}

			Collections.sort(movies);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return movies;
	}

	private void setCommentWithFiveStarsOrMore(Document pageMovie, Movie movie) {
		try {
			String comment = "";
			Integer starsToFilter = 5;
			String urlMovieReview = pageMovie.getElementsByClass("UserReviewsHeader__Header-k61aee-0 egCnbs")
					.select("a").attr("href").toString();

			while (comment.isBlank() && starsToFilter < 11) {
				String urlMovieReviewToConect = buildUrlReview(urlMovieReview) + starsToFilter;
				Document pageMovieReviewByStars = Jsoup.connect(urlMovieReviewToConect).get();

				Elements pageMovieReviewByStarsElements = pageMovieReviewByStars
						.getElementsByClass("lister-item mode-detail imdb-user-review  collapsable");

				if (pageMovieReviewByStarsElements.size() > 0) {
					comment = pageMovieReviewByStarsElements.first().getElementsByClass("text show-more__control")
							.text();
				}
				starsToFilter++;
			}

			movie.setComment(comment);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String buildUrlReview(String urlMovieReview) {
		StringBuilder sb = new StringBuilder(urlMovieReview);
		sb.delete(urlMovieReview.indexOf("?") + 1, urlMovieReview.length());
		urlMovieReview = sb.toString();
		urlMovieReview = urlBase + urlMovieReview + urlReviewByStars;
		return urlMovieReview;
	}

	private void setTopCast(Document pageMovie, Movie movie) {
		List<String> topCastList = new ArrayList<>();

		Elements topCast = pageMovie
				.getElementsByClass(
						"ipc-sub-grid ipc-sub-grid--page-span-2 ipc-sub-grid--wraps-at-above-l title-cast__grid")
				.select("div.StyledComponents__CastItemWrapper-y9ygcu-7");

		for (int i = 0; i < topCast.size(); i++) {
			String name = topCast.get(i).getElementsByClass("StyledComponents__CastItemSummary-y9ygcu-9 fBAofn")
					.select("a.StyledComponents__ActorName-y9ygcu-1").text();
			topCastList.add(name);
		}
		movie.setTopCast(topCastList);
	}

	private void setTitle(Document pageMovie, Movie movie) {
		String title = pageMovie.getElementsByClass("TitleHeader__TitleText-sc-1wu6n3d-0 dxSWFG").text();
		movie.setTitle(title);
	}

	private void setOriginalTitle(Document pageMovie, Movie movie) {
		String originalTitle = pageMovie.getElementsByClass("OriginalTitle__OriginalTitleText-jz9bzr-0 llYePj").text()
				.replace("Original title: ", "");
		movie.setOriginalTitle(originalTitle);
	}

	private void setRating(Document pageMovie, Movie movie) {
		Double rating = Double.parseDouble(
				pageMovie.getElementsByClass("AggregateRatingButton__RatingScore-sc-1ll29m0-1 iTLWoV").first().text());
		movie.setRating(rating);
	}

	private void setDirectorsList(Document pageMovie, Movie movie) {
		List<String> directorsList = new ArrayList<>();

		Elements directorsElementList = pageMovie.getElementsByClass(
				"ipc-metadata-list ipc-metadata-list--dividers-all StyledComponents__CastMetaDataList-y9ygcu-10 cbPPkN ipc-metadata-list--base")
				.select("li").first()
				.getElementsByClass(
						"ipc-inline-list ipc-inline-list--show-dividers ipc-inline-list--inline ipc-metadata-list-item__list-content base")
				.select("li");

		for (int i = 0; i < directorsElementList.size(); i++) {
			String directorName = directorsElementList.get(i).select("a").text();
			directorsList.add(directorName);
		}
		movie.setDirectors(directorsList);
	}

}
