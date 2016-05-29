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
    private int total_results;
    private int total_pages;
    private List<MovieContract> results;

    public Result(){
        this.results = new ArrayList<>();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieContract> getMoviesList() {
        return results;
    }

    public void setMoviesList(List<MovieContract> moviesList) {
        this.results = moviesList;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
}