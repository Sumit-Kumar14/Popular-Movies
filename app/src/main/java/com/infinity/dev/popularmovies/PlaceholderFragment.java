package com.infinity.dev.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceholderFragment extends Fragment implements FetchDataListener{

    private static final String TAG = PlaceholderFragment.class.getSimpleName();
    Result resultObj = new Result();
    GridView moviesGrid;
    CustomAdapter adapter;
    boolean loading = true;
    SwipeRefreshLayout swipeRefreshLayout;
    String type;
    String baseURL;

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
                detailIntent.putExtra("Details", resultObj.getMoviesList().get(position));
                startActivity(detailIntent);
            }
        });

        if(type.equals("popular")) {
            baseURL = "http://api.themoviedb.org/3/movie/";
        }else if(type.equals("top_rated")) {
            baseURL = "http://api.themoviedb.org/3/movie/" + "top_rated" + "?api_key=";
        }
        moviesGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!loading && firstVisibleItem + visibleItemCount >= totalItemCount){
                    if(resultObj.getPage() < resultObj.getTotal_pages()){
                        loading = true;
                        new FetchDataTask(getActivity(), PlaceholderFragment.this).execute(baseURL + Constants.API_KEY + "&page=" + (resultObj.getPage() + 1));
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

    @Override
    public void onFetchCompletion(String string) {
        swipeRefreshLayout.setRefreshing(false);
        if(string == null) {
            Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return;
        }
        loading = false;
        try{
            JSONObject root = new JSONObject(string);
            int page = root.getInt("page");
            int total_results = root.getInt("total_results");
            int total_pages = root.getInt("total_pages");
            JSONArray results = root.getJSONArray("results");
            for(int i = 0; i < results.length(); i++){
                MovieContract contract = new MovieContract();
                JSONObject result = results.getJSONObject(i);
                String poster_path = result.getString("poster_path");
                boolean adult = result.getBoolean("adult");
                String overview = result.getString("overview");
                String release_date = result.getString("release_date");
                int id = result.getInt("id");
                String original_title = result.getString("original_title");
                String original_language = result.getString("original_language");
                double vote_average = result.getDouble("vote_average");
                contract.setPoster_path(poster_path);
                contract.setAdult(adult);
                contract.setOverview(overview);
                contract.setRelease_date(release_date);
                contract.setId(id);
                contract.setOriginal_title(original_title);
                contract.setOriginal_language(original_language);
                contract.setVote_average(vote_average);
                resultObj.getMoviesList().add(contract);
            }
            resultObj.setPage(page);
            resultObj.setTotal_pages(total_pages);
            resultObj.setTotal_results(total_results);
            adapter.notifyDataSetChanged();
            loading = false;
        }catch (JSONException ex){
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void updateMovieGrid() {
        //Scroll to the first element in the grid
        moviesGrid.setSelection(0);
        //Clear the previous movies list
        resultObj.getMoviesList().clear();
        //Set the current viewing page to 1
        resultObj.setPage(1);
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .build();

        MoviesAPI api = retrofit.create(MoviesAPI.class);

        api.getMovie().enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int i = response.body().getTotal_pages();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }
}