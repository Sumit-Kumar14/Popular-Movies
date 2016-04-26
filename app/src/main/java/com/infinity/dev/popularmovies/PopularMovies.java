package com.infinity.dev.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PopularMovies extends AppCompatActivity implements FetchDataListener{

    private static final String TAG = PopularMovies.class.getSimpleName();
    Result resultObj = new Result();
    GridView moviesGrid;
    CustomAdapter adapter;
    boolean loading = true;
    SwipeRefreshLayout swipeRefreshLayout;

    String sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        moviesGrid = (GridView) findViewById(R.id.moviesGrid);
        adapter = new CustomAdapter(this, resultObj.getMoviesList());
        moviesGrid.setAdapter(adapter);
        moviesGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(!loading && firstVisibleItem + visibleItemCount >= totalItemCount){
                    if(resultObj.getPage() < resultObj.getTotalPages()){
                        loading = true;
                        new FetchDataTask(PopularMovies.this, PopularMovies.this).execute("http://api.themoviedb.org/3/" + sortOrder + "?api_key=" + Constants.API_KEY + "&page=" + (resultObj.getPage() + 1));
                    }
                }
            }
        });
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(PopularMovies.this, MovieDetail.class);
                detailIntent.putExtra("Details", resultObj.getMoviesList().get(position));
                startActivity(detailIntent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortOrder = prefs.getString(getString(R.string.pref_key),
                getString(R.string.movie_popular));
        updateMovieGrid();
    }

    private void updateMovieGrid() {
        //Scroll to the first element in the grid
        moviesGrid.setSelection(0);
        //Clear the previous movies list
        resultObj.getMoviesList().clear();
        //Set the current viewing page to 1
        resultObj.setPage(1);
        new FetchDataTask(this, this).execute("http://api.themoviedb.org/3/" + sortOrder + "?api_key=" + Constants.API_KEY + "&page=" + 1);
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

    @Override
    public void onFetchCompletion(String string) {
        swipeRefreshLayout.setRefreshing(false);
        if(string == null) {
            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
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
            resultObj.setTotalPages(total_pages);
            resultObj.setTotalResults(total_results);
            adapter.notifyDataSetChanged();
            loading = false;
        }catch (JSONException ex){
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
    }
}