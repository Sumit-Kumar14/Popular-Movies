package com.infinity.dev.popularmovies;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailFragment extends Fragment {
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
        genre = (TextView) view.findViewById(R.id.genre);
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

        String id = getArguments().getString("ID");

        api.getMovieDetails(id).enqueue(new Callback<MovieContract>() {
            @Override
            public void onResponse(Call<MovieContract> call, Response<MovieContract> response) {
                Picasso.with(getActivity()).load(BASE_URL + SIZE + response.body().getPoster_path()).into(poster);
                movieName.setText(response.body().getOriginal_title());
                releaseDate.setText(response.body().getRelease_date());
                overview.setText(response.body().getOverview());
                Picasso.with(getActivity()).load(BASE_URL + THUMBNAIL_SIZE + response.body().getPoster_path()).into(thumbnail);
                ratingBar.setRating((float)(response.body().getVote_average()/2));
                year.setText(response.body().getRelease_date());
                StringBuilder genreStr = new StringBuilder();
                MovieContract.Genres[] genreArray = response.body().getGenres();
                for (int i = 0; i < genreArray.length; i++) {
                    genreStr.append(genreArray[i].getName());
                    if (i < genreArray.length - 1)
                        genreStr.append(" | ");
                }
                censor.setText(response.body().isAdult() ? "A" : "UA");
                genre.setText(genreStr.toString());
                if(response.body().getTagline() != null || response.body().getTagline().length() != 0)
                    tagline.setText(response.body().getTagline());
                else
                    tagline.setText("N/A");
                duration.setText(response.body().getRuntime() / 60 + " hrs " + response.body().getRuntime() % 60 + " mins");
            }

            @Override
            public void onFailure(Call<MovieContract> call, Throwable t) {
                t.printStackTrace();
                if(t instanceof IOException)
                    showSnack(view, "No or poor internet connection", R.color.red);
                else
                    showSnack(view, "Something went wrong. Please try again later.", R.color.red);
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
}
