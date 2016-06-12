package com.infinity.dev.popularmovies;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import database.DBHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailFragment extends Fragment {
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

    FloatingActionButton save;
    MovieContract contract;
    DBHelper helper;

    private static final String BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String SIZE = "w500";
    private static final String THUMBNAIL_SIZE = "w320";

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .build();
    MoviesAPI api = retrofit.create(MoviesAPI.class);

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.movie_detail_fragment, container, false);
        movieName = (TextView) view.findViewById(R.id.movie_name);
        ratingBar = (RatingBar) view.findViewById(R.id.rating);
        year = (TextView) view.findViewById(R.id.movie_year);
        overview = (ExpandableTextView) view.findViewById(R.id.overview);
        poster = (ImageView) view.findViewById(R.id.poster);
        thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        censor = (TextView) view.findViewById(R.id.censor);
        duration = (TextView) view.findViewById(R.id.duration);
        releaseDate = (TextView) view.findViewById(R.id.release);
        tagline = (TextView) view.findViewById(R.id.tagline);

        save = (FloatingActionButton) view.findViewById(R.id.save);

        final String id = getArguments().getString("ID");

        helper = new DBHelper(getActivity());

        if(helper.isFavourite(id)) {
            save.setImageResource(R.drawable.ic_favorite_white_24dp);
        }else {
            save.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        helper.close();

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
                helper = new DBHelper(getActivity());
                if(helper.isFavourite(id)) {
                    helper.removeFromFavourite(id);
                    save.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Toast.makeText(getActivity(), getResources().getString(R.string.movie_removed), Toast.LENGTH_LONG).show();
                }else {
                    if(contract != null) {
                        long result = helper.addToFavourite(contract);
                        if (result != -1) {
                            save.setImageResource(R.drawable.ic_favorite_white_24dp);
                            Toast.makeText(getActivity(), getResources().getString(R.string.movie_added), Toast.LENGTH_LONG).show();
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
        getChildFragmentManager().beginTransaction().replace(R.id.video_frames, videos).commit();

        GenericFragment casts = new GenericFragment();
        Bundle castBundle = new Bundle();
        castBundle.putString("TYPE", "CASTS");
        castBundle.putString("HEADING", "Casting");
        castBundle.putString("ID", id);
        casts.setArguments(castBundle);
        getChildFragmentManager().beginTransaction().replace(R.id.casting, casts).commit();

        ReviewFragment fragment = new ReviewFragment();
        Bundle reviewBundle = new Bundle();
        reviewBundle.putString("ID", id);
        fragment.setArguments(reviewBundle);
        getChildFragmentManager().beginTransaction().replace(R.id.reviews, fragment).commit();
        return view;
    }

    public Snackbar showSnack(View view, String msg, int color) {
        Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snack.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        ViewGroup group = (ViewGroup) snack.getView();
        TextView tv = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        group.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snack.show();
        return snack;
    }

    public void updateUI(MovieContract contract) {
        Picasso.with(getActivity()).load(BASE_URL + SIZE + contract.getPoster_path()).into(poster);
        movieName.setText(contract.getOriginal_title());
        releaseDate.setText(contract.getRelease_date());
        overview.setText(contract.getOverview());
        Picasso.with(getActivity()).load(BASE_URL + THUMBNAIL_SIZE + contract.getPoster_path()).into(thumbnail);
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
