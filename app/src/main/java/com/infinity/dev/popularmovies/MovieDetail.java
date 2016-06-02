package com.infinity.dev.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetail extends AppCompatActivity {

    View view;
    TextView genre;
    TextView movieName;
    RatingBar ratingBar;
    TextView year;
    ExpandableTextView overview;
    ImageView poster;
    ImageView thumbnail;
    TextView censor;
    TextView duration;
    TextView releaseDate;
    TextView tagline;
    int movieId;

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE = "w500";
    private static final String THUMBNAIL_SIZE = "w320";

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .build();
    MoviesAPI api = retrofit.create(MoviesAPI.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        genre = (TextView) findViewById(R.id.genre);
        movieName = (TextView) findViewById(R.id.movie_name);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        year = (TextView) findViewById(R.id.movie_year);
        overview = (ExpandableTextView) findViewById(R.id.overview);
        poster = (ImageView) findViewById(R.id.poster);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        censor = (TextView) findViewById(R.id.censor);
        duration = (TextView) findViewById(R.id.duration);
        releaseDate = (TextView) findViewById(R.id.release);
        tagline = (TextView) findViewById(R.id.tagline);

        Intent intent = getIntent();
        String id = intent.getStringExtra("ID");

        api.getMovieDetails(id).enqueue(new Callback<MovieContract>() {
            @Override
            public void onResponse(Call<MovieContract> call, Response<MovieContract> response) {
                Picasso.with(MovieDetail.this).load(BASE_URL + SIZE + response.body().getPoster_path()).into(poster);
                movieName.setText(response.body().getOriginal_title());
                releaseDate.setText(response.body().getRelease_date());
                overview.setText(response.body().getOverview());
                Picasso.with(MovieDetail.this).load(BASE_URL + THUMBNAIL_SIZE + response.body().getPoster_path()).into(thumbnail);
                ratingBar.setRating((float)(response.body().getVote_average()/2));
                year.setText(response.body().getRelease_date());
            }

            @Override
            public void onFailure(Call<MovieContract> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}