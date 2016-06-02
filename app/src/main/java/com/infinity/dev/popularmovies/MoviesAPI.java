package com.infinity.dev.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesAPI {

    @GET("{type}?api_key=" + Constants.API_KEY)
    Call<Result> getMovies(@Path("type") String path, @Query("page") int page);

    @GET("{id}?api_key=" +Constants.API_KEY)
    Call<MovieContract> getMovieDetails(@Path("id") String id);
}