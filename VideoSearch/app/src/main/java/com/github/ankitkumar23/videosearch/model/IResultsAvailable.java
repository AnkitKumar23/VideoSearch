package com.github.ankitkumar23.videosearch.model;

/**
 * Interface for listening to the results
 * Created by AnkitKumar23 on 10/23/2015.
 */
public interface IResultsAvailable {
    public static final int SUCCESS = 0;
    public static final int EXCEPTION = 1;
    public void onResultsAvailable(QueryResult results, int error);
}