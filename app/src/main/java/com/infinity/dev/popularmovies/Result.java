package com.infinity.dev.popularmovies;

import java.util.ArrayList;
import java.util.List;

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