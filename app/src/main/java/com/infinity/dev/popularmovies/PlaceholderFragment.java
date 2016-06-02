package com.infinity.dev.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceholderFragment extends Fragment{

    private static final String TAG = PlaceholderFragment.class.getSimpleName();
    Result resultObj = new Result();
    GridView moviesGrid;
    CustomAdapter adapter;
    boolean loading = true;
    SwipeRefreshLayout swipeRefreshLayout;
    String type;
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .build();
    MoviesAPI api = retrofit.create(MoviesAPI.class);

    public PlaceholderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        type = getArguments().getString("TYPE");
        moviesGrid = (GridView) rootView.findViewById(R.id.moviesGrid);
        adapter = new CustomAdapter(getActivity(), resultObj.getMoviesList());
        moviesGrid.setAdapter(adapter);

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(getActivity(), MovieDetail.class);
                detailIntent.putExtra("ID", resultObj.getMoviesList().get(position).getId());
                startActivity(detailIntent);
            }
        });

        moviesGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!loading && firstVisibleItem + visibleItemCount >= totalItemCount){
                    if(resultObj.getPage() < resultObj.getTotal_pages()){
                        loading = true;
                        if(type.equals("POPULAR")) {
                            getMovies("popular", resultObj.getPage() + 1);
                        }else if(type.equals("TOP_RATED")) {
                            getMovies("top_rated", resultObj.getPage() + 1);
                        }
                    }
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        if(swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.green);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);
                    updateMovieGrid();
                }
            });
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieGrid();
    }

    private void updateMovieGrid() {
        //Scroll to the first element in the grid
        moviesGrid.setSelection(0);
        //Clear the previous movies list
        resultObj.getMoviesList().clear();
        //Set the current viewing page to 1
        resultObj.setPage(1);

        if(type.equals("POPULAR"))
            getMovies("popular", 1);
        else if(type.equals("TOP_RATED"))
            getMovies("top_rated", 1);
    }

    private void getMovies(String type, int page) {
        api.getMovies(type, page).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                swipeRefreshLayout.setRefreshing(false);
                resultObj.setPage(response.body().getPage());
                resultObj.setTotal_pages(response.body().getTotal_pages());
                resultObj.setTotal_results(response.body().getTotal_results());
                resultObj.getMoviesList().addAll(response.body().getMoviesList());
                adapter.notifyDataSetChanged();
                loading = false;
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}