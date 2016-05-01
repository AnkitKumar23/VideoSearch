package com.github.ankitkumar23.videosearch.datastore;

import android.app.Fragment;
import android.os.Bundle;

import com.github.ankitkumar23.videosearch.model.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Mainly used for saving data across configuration changes.
 * This fragment doesn't have an UI.
 * Created by AnkitKumar23 on 10/23/2015.
 */
public class RetainedFragment extends Fragment {
    private boolean mIsLoadingComplete;
    private int nextPage;
    private List<QueryResult.Result> mResults;
    private String query;
    public RetainedFragment() {
        if(mResults == null) {
            mResults = new ArrayList<QueryResult.Result>();
        }
        resetNextPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<QueryResult.Result> getData() {
        return mResults;
    }

    public QueryResult.Result getDataItem(int pos) {
        return mResults.get(pos);
    }

    public void addData(List<QueryResult.Result> data) {
        mResults.addAll(data);
    }

    public void addDataItem(QueryResult.Result dataItem) {
        mResults.add(dataItem);
    }

    public void resetData() {
        mResults.clear();
    }
    public void resetLoadComplete() {
        mIsLoadingComplete = false;
    }
    public void setLoadComplete() {
        mIsLoadingComplete = true;
    }

    public boolean isLoadComplete() {
        return mIsLoadingComplete;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String _query) {
        query = _query;
    }

    public void setNextPage(int _nextPage) {
        nextPage = _nextPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void resetNextPage() {
        nextPage = 1;
    }
}
