package com.github.ankitkumar23.videosearch.model;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * class for fetching data and configuring requests
 * Created by AnkitKumar23 on 10/23/2015.
 */
public final class QueryService {
    private static final String LOG_TAG = "QueryService";
    private boolean IS_RUNNING = false;
    private static final String API_URL = "https://api.vimeo.com";
    private final int RESULTS_PER_PAGE = 10;
    private final int RESULT_200_OK = 200;
    final String token = "23936722e4232b28675a49cfedc19b60";

    Retrofit mRetrofit;
    public QueryService() {

        //configure retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Queries the given url and fetches the data
     * @param query query word
     * @param page page index to return
     * @param iResultsAvailable callback to populate results
     */
    public void query(String query, int page, final IResultsAvailable iResultsAvailable)  {
        Log.d(LOG_TAG, "Received query: " + query + " page: "+ page);
        QueryResultFetcher qrf = mRetrofit.create(QueryResultFetcher.class);
        Map<String, String> options = new HashMap<String, String>();
        options.put("page", String.valueOf(page));
        options.put("per_page", String.valueOf(RESULTS_PER_PAGE));
        options.put("query", query);

        final String basic =
                //"Bearer " + Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
                "bearer "+token;
        Call<QueryResult> call = qrf.query(options, basic);
        IS_RUNNING = true;
        call.enqueue(new Callback<QueryResult>() {
            @Override
            public void onResponse(Response<QueryResult> response, Retrofit retrofit) {
                Log.d(LOG_TAG, "Received response: "+ response.code());
                IS_RUNNING = false;
                if(response.code() == RESULT_200_OK) {
                    iResultsAvailable.onResultsAvailable(response.body(), IResultsAvailable.SUCCESS);
                } else {
                    iResultsAvailable.onResultsAvailable(null, IResultsAvailable.EXCEPTION);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(LOG_TAG, "Received exception");
                iResultsAvailable.onResultsAvailable(null, IResultsAvailable.EXCEPTION);
                IS_RUNNING = false;
            }
        });
    }

    /**
     * returns whether current query is running
     * @return boolean
     */
    public boolean isRunning() {
        return IS_RUNNING;
    }
}
