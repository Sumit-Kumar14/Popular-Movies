package com.infinity.dev.popularmovies;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MoviesAPI {
    @GET("top_rated" + "?api_key=" + Constants.API_KEY + "&page=" + 1  )
    Call<Result> getMovie();
}