package com.infinity.dev.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesAPI {

    @GET("{type}?api_key=" + Constants.API_KEY)
    Call<Result> getMovies(@Path("type") String path, @Query("page") int page);

    @GET("{id}?api_key=" + Constants.API_KEY)
    Call<MovieContract> getMovieDetails(@Path("id") String id);

    @GET("{id}/videos?api_key=" + Constants.API_KEY)
    Call<MovieVideosContract> getMovieVideos(@Path("id") String id);

    @GET("{id}/credits?api_key=" + Constants.API_KEY)
    Call<CastingContract> getCasts(@Path("id") String id);

    @GET("{id}/reviews?api_key=" + Constants.API_KEY)
    Call<ReviewsContract> getReviews(@Path("id") String id);
}