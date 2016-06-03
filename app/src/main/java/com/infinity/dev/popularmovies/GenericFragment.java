package com.infinity.dev.popularmovies;

/**
 * Created by sumitkumar on 3/6/16.
 */
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenericFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    String heading;
    String type;

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .build();
    MoviesAPI api = retrofit.create(MoviesAPI.class);

    GenericAdapter adapter;
    MovieVideosContract videos;
    CastingContract casts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container,savedInstanceState);
        view = inflater.inflate(R.layout.fragment_layout, container, false);
        type = getArguments().getString("TYPE");

        recyclerView = (RecyclerView) view.findViewById(R.id.main_content);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        final Context context = getActivity();
        if(context != null) {
            if (type != null && type.equals("VIDEOS")) {
                String id = getArguments().getString("ID");
                if (id != null) {
                    api.getMovieVideos(id).enqueue(new Callback<MovieVideosContract>() {
                        @Override
                        public void onResponse(Call<MovieVideosContract> call, Response<MovieVideosContract> response) {
                            videos = response.body();
                            TextView header = (TextView) view.findViewById(R.id.header);
                            heading = getArguments().getString("HEADING");
                            header.setText(heading);

                            adapter = new GenericAdapter(context, videos.results);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Call<MovieVideosContract> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            } else if (type != null && type.equals("CASTS")) {
                String id = getArguments().getString("ID");
                if (id != null) {
                    api.getCasts(id).enqueue(new Callback<CastingContract>() {
                        @Override
                        public void onResponse(Call<CastingContract> call, Response<CastingContract> response) {
                            casts = response.body();
                            TextView header = (TextView) view.findViewById(R.id.header);
                            heading = getArguments().getString("HEADING");
                            header.setText(heading);

                            adapter = new GenericAdapter(context, casts.getCasts());
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Call<CastingContract> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        }
        return view;
    }

    public void showSnack(View view, String msg, int color) {
        Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snack.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        ViewGroup group = (ViewGroup) snack.getView();
        TextView tv = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        group.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
        snack.show();
    }
}