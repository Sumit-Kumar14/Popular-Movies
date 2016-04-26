package com.infinity.dev.popularmovies;

import java.util.ArrayList;
import java.util.List;

/**
 * Result class will contain the raw JSON data that came from the server.
 * Its structure is similar to the result returned by the server by querying
 * /movie/popular or /movie/top_rated.
 */

public class Result{
    private int page;
    private int totalResults;
    private int totalPages;
    private List<MovieContract> moviesList;

    public Result(){
        this.moviesList = new ArrayList<>();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<MovieContract> getMoviesList() {
        return moviesList;
    }

    public void setMoviesList(List<MovieContract> moviesList) {
        this.moviesList = moviesList;
    }
}