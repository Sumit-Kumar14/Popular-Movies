package com.infinity.dev.popularmovies;

public interface FetchDataListener {
    void onFetchCompletion(String string);
    void onError(Exception ex);
}
