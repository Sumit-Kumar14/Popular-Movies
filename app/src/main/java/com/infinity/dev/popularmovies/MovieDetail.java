package com.infinity.dev.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import database.DBHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetail extends AppCompatActivity {

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

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE = "w500";
    private static final String THUMBNAIL_SIZE = "w320";

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .build();
    MoviesAPI api = retrofit.create(MoviesAPI.class);

    FloatingActionButton save;
    MovieContract contract;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        save = (FloatingActionButton) findViewById(R.id.save);

        helper = new DBHelper(this);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("ID");

        if(helper.isFavourite(id)) {
            save.setImageResource(R.drawable.ic_favorite_white_24dp);
            Cursor cursor = helper.getMovieDetail(id);
            if(cursor.moveToFirst()) {
                do {
                    contract = new MovieContract();
                    contract.setAdult(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("ADULT"))));
                    contract.setBackdrop_path(cursor.getString(cursor.getColumnIndex("BACKDROP_PATH")));
                    contract.setHomepage(cursor.getString(cursor.getColumnIndex("HOMEPAGE")));
                    contract.setId(cursor.getString(cursor.getColumnIndex("ID")));
                    contract.setImdb_id(cursor.getString(cursor.getColumnIndex("IMDB_ID")));
                    contract.setOriginal_language(cursor.getString(cursor.getColumnIndex("ORIGINAL_LANGUAGE")));
                    contract.setOriginal_title(cursor.getString(cursor.getColumnIndex("ORIGINAL_TITLE")));
                    contract.setOverview(cursor.getString(cursor.getColumnIndex("OVERVIEW")));
                    contract.setPopularity(cursor.getDouble(cursor.getColumnIndex("POPULARITY")));
                    contract.setPoster_path(cursor.getString(cursor.getColumnIndex("POSTER_PATH")));
                    contract.setRelease_date(cursor.getString(cursor.getColumnIndex("RELEASE_DATE")));
                    contract.setRuntime(cursor.getInt(cursor.getColumnIndex("RUNTIME")));
                    contract.setStatus(cursor.getString(cursor.getColumnIndex("STATUS")));
                    contract.setTagline(cursor.getString(cursor.getColumnIndex("TAGLINE")));
                    contract.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));
                    contract.setVote_count(cursor.getInt(cursor.getColumnIndex("VOTE_AVERAGE")));
                    contract.setVote_count(cursor.getInt(cursor.getColumnIndex("VOTE_COUNT")));

                    updateUI(contract);
                }while (cursor.moveToNext());
            }
        }else {
            save.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            api.getMovieDetails(id).enqueue(new Callback<MovieContract>() {
                @Override
                public void onResponse(Call<MovieContract> call, Response<MovieContract> response) {
                    contract = response.body();
                    updateUI(contract);
                }

                @Override
                public void onFailure(Call<MovieContract> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        helper.close();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper = new DBHelper(MovieDetail.this);
                if(helper.isFavourite(id)) {
                    helper.removeFromFavourite(id);
                    save.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Toast.makeText(MovieDetail.this, getResources().getString(R.string.movie_removed), Toast.LENGTH_LONG).show();
                }else {
                    if(contract != null) {
                        long result = helper.addToFavourite(contract);
                        if (result != -1) {
                            save.setImageResource(R.drawable.ic_favorite_white_24dp);
                            Toast.makeText(MovieDetail.this, getResources().getString(R.string.movie_added), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                helper.close();
            }
        });

        GenericFragment videos = new GenericFragment();
        Bundle videoBundle = new Bundle();
        videoBundle.putString("TYPE", "VIDEOS");
        videoBundle.putString("HEADING", "Movie Trailers");
        videoBundle.putString("ID", id);
        videos.setArguments(videoBundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.video_frames, videos).commit();

        GenericFragment casts = new GenericFragment();
        Bundle castBundle = new Bundle();
        castBundle.putString("TYPE", "CASTS");
        castBundle.putString("HEADING", "Casting");
        castBundle.putString("ID", id);
        casts.setArguments(castBundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.casting, casts).commit();

        ReviewFragment fragment = new ReviewFragment();
        Bundle reviewBundle = new Bundle();
        reviewBundle.putString("ID", id);
        fragment.setArguments(reviewBundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.reviews, fragment).commit();
    }

    public void updateUI(MovieContract contract) {
        Picasso.with(MovieDetail.this).load(BASE_URL + SIZE + contract.getPoster_path()).into(poster);
        movieName.setText(contract.getOriginal_title());
        releaseDate.setText(contract.getRelease_date());
        overview.setText(contract.getOverview());
        Picasso.with(MovieDetail.this).load(BASE_URL + THUMBNAIL_SIZE + contract.getPoster_path()).into(thumbnail);
        ratingBar.setRating((float)(contract.getVote_average()/2));
        year.setText(contract.getRelease_date());
        censor.setText(contract.isAdult() ? "A" : "UA");
        if(contract.getTagline() != null || contract.getTagline().length() != 0)
            tagline.setText(contract.getTagline());
        else
            tagline.setText("N/A");
        duration.setText(contract.getRuntime() / 60 + " hrs " + contract.getRuntime() % 60 + " mins");
    }
}