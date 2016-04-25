package com.infinity.dev.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    ImageView posterImage;
    TextView title;
    TextView releaseDate;
    TextView overview;
    TextView voteAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        posterImage = (ImageView) findViewById(R.id.poster);
        title = (TextView) findViewById(R.id.title);
        releaseDate = (TextView) findViewById(R.id.releaseDate);
        overview = (TextView) findViewById(R.id.overview);
        voteAverage = (TextView) findViewById(R.id.vote_average);

        Intent intent = getIntent();
        MovieContract contract = (MovieContract) intent.getSerializableExtra("Details");
        String BASE_URL = "http://image.tmdb.org/t/p/";
        String SIZE = "w500";
        Picasso.with(this).load(BASE_URL + SIZE + contract.getPoster_path()).into(posterImage);
        title.setText(contract.getOriginal_title());
        releaseDate.setText(contract.getRelease_date());
        overview.setText(contract.getOverview());
        voteAverage.setText(getResources().getString(R.string.vote_average, contract.getVote_average()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}