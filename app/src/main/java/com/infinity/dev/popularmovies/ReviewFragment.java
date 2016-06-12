package com.infinity.dev.popularmovies;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sumitkumar on 6/6/16.
 */

public class ReviewFragment extends Fragment {

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .build();
    MoviesAPI api = retrofit.create(MoviesAPI.class);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_fragment, container, false);
        final LinearLayout reviews = (LinearLayout) view.findViewById(R.id.reviews);

        final LayoutInflater reviewsInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        reviews.removeAllViewsInLayout();

        String id = getArguments().getString("ID");

        api.getReviews(id).enqueue(new Callback<ReviewsContract>() {
            @Override
            public void onResponse(Call<ReviewsContract> call, Response<ReviewsContract> response) {
                for(int i = 0; i < response.body().getResults().size(); i++) {
                    View reviewsItem = reviewsInflater.inflate(R.layout.review_item, null, false);
                    TextView initial = (TextView) reviewsItem.findViewById(R.id.initial);
                    TextView name = (TextView) reviewsItem.findViewById(R.id.name);
                    TextView content = (TextView) reviewsItem.findViewById(R.id.review);
                    initial.setText(String.valueOf(response.body().getResults().get(i).getAuthor().toUpperCase().charAt(0)));
                    name.setText(response.body().getResults().get(i).getAuthor());
                    content.setText(response.body().getResults().get(i).getContent());
                    reviews.addView(reviewsItem);
                }
            }

            @Override
            public void onFailure(Call<ReviewsContract> call, Throwable t) {
                t.printStackTrace();
                if(t instanceof IOException)
                    showSnack(getView(), "No or poor internet connection", R.color.red);
                else
                    showSnack(getView(), "Something went wrong. Please try again later.", R.color.red);
            }
        });
        return view;
    }

    public void showSnack(View view, String msg, int color) {
        if(view != null) {
            Snackbar snack = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
            snack.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            ViewGroup group = (ViewGroup) snack.getView();
            TextView tv = (TextView) group.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            group.setBackgroundColor(ContextCompat.getColor(getActivity(), color));
            snack.show();
        }
    }
}