package com.github.ankitkumar23.videosearch.ui;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ankitkumar23.videosearch.R;
import com.github.ankitkumar23.videosearch.adapter.CustomAdapter;
import com.github.ankitkumar23.videosearch.datastore.RetainedFragment;
import com.github.ankitkumar23.videosearch.model.IResultsAvailable;
import com.github.ankitkumar23.videosearch.model.QueryResult;
import com.github.ankitkumar23.videosearch.model.QueryService;
import com.github.ankitkumar23.videosearch.provider.RecentQueryProvider;
import com.squareup.picasso.Picasso;

/**
 * Main activity for showing the list of video search results
 */
public class VideoResultListActivity extends AppCompatActivity implements IResultsAvailable {
    private ListView mListView;
    private RetainedFragment mDataFragment;
    private QueryService mQs;
    private CustomAdapter mAdapter;
    private static final String RETAINED_FRAGMENT_TAG = "video";
    private static final String TAG = "VideoResultListActivity";
    private ProgressBar mProgressBar;
    private ProgressBar mMainProgressBar;
    private ImageView mDefaultImageView;
    private View mFooterView;
    private String IS_MAIN_PROGRESS_VISIBLE = "is_main_progress_visible";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_result_list);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FragmentManager fm = getFragmentManager();
        mDataFragment = (RetainedFragment) fm.findFragmentByTag(RETAINED_FRAGMENT_TAG);
        if(mDataFragment == null) {
            mDataFragment = new RetainedFragment();
            fm.beginTransaction().add(mDataFragment, RETAINED_FRAGMENT_TAG).commit();
        }
        mListView = (ListView) findViewById(R.id.listView);
        if(savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_MAIN_PROGRESS_VISIBLE)) {
            mMainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
            mListView.setEmptyView(mMainProgressBar);
        } else {
            mDefaultImageView = (ImageView) findViewById(R.id.image_background);
            mListView.setEmptyView(mDefaultImageView);
        }
        init();
    }

    /**
     * initialize the system
     */
    private void init() {
        mQs = new QueryService();
        Log.d(TAG, "Inside Init: ");
        mAdapter = new CustomAdapter(this, R.layout.list_item_layout, mDataFragment.getData());
        mListView.setAdapter(mAdapter);
        mFooterView = LayoutInflater.from(this).inflate(R.layout.progress_bar_layout, null);
        mProgressBar = (ProgressBar) mFooterView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mListView.addFooterView(mFooterView);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final Picasso picasso = Picasso.with(VideoResultListActivity.this);
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(VideoResultListActivity.this);
                } else {
                    picasso.pauseTag(VideoResultListActivity.this);
                }
                if (scrollState == SCROLL_STATE_IDLE) {
                    Log.d(TAG, "last visible: " + mListView.getLastVisiblePosition());
                    if (mListView.getLastVisiblePosition() >= mDataFragment.getData().size() - 1) {
                        if (canFetchMore()) {
                            if(!isFooterProgressVisible()) {
                                mProgressBar.setVisibility(View.VISIBLE);
                            }
                            mQs.query(mDataFragment.getQuery(), mDataFragment.getNextPage(), VideoResultListActivity.this);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //starts the detailed video view
                QueryResult.Result result = mDataFragment.getDataItem(position);
                String[] parts = result.uri.split("/");
                Intent intent = new Intent(VideoResultListActivity.this, VideoDetailActivity.class);
                intent.putExtra("id", parts[2]);
                startActivity(intent);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    /**
     * returns whether bottom progress bar is visible
     * @return
     */
    private boolean isFooterProgressVisible() {
        return mProgressBar != null && (mProgressBar.getVisibility() == View.VISIBLE);
    }

    /**
     * change visibility of default Image view
     * @param visible
     */
    private void setDefaultImageViewVisibility(int visible) {
        if(mDefaultImageView != null)
            mDefaultImageView.setVisibility(visible);
    }

    /**
     * change visibility of default progressbar
     * @param visible
     */
    private void setMainProgressBarVisibility(int visible) {
        if(mMainProgressBar != null)
            mMainProgressBar.setVisibility(visible);
    }
    /**
     * Handles new search query
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.ACTION_SEARCH.equals(intent.getAction())) {
            mDataFragment.resetData();
            mDataFragment.resetLoadComplete();
            mDataFragment.resetNextPage();
            init();
            setDefaultImageViewVisibility(View.GONE);
            mMainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
            setMainProgressBarVisibility(View.VISIBLE);
            mListView.setEmptyView(mMainProgressBar);

            mDataFragment.setQuery(intent.getStringExtra(SearchManager.QUERY));
            Log.d(TAG, "Received query: " + mDataFragment.getQuery());
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    RecentQueryProvider.AUTHORITY, RecentQueryProvider.MODE);
            suggestions.saveRecentQuery(mDataFragment.getQuery(), null);

            if(canFetchMore())
                mQs.query(mDataFragment.getQuery(), mDataFragment.getNextPage(), this);
        }
    }

    /**
     * check if main progressbar is visible
     * @return
     */
    private boolean isMainProgressBarVisible() {
        return (mMainProgressBar != null) &&
                (mMainProgressBar.getVisibility() == View.VISIBLE);
    }

    /**
     * call back indicating fetched results
     * @param results
     * @param error
     */
    @Override
    public void onResultsAvailable(QueryResult results, int error) {
        if(isFooterProgressVisible()) {
            mProgressBar.setVisibility(View.GONE);
        }
        setMainProgressBarVisibility(View.GONE);

        //Handle network error
        if(error != IResultsAvailable.SUCCESS) {
            Log.d(TAG, "Page load error");
            Toast.makeText(VideoResultListActivity.this, "Page Load error", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Received results");
        //check if there are no results for a query
        if(results.page == 1 && (results.data == null ||
                results.data.size() == 0)) {
            mDataFragment.setLoadComplete();
            mDataFragment.resetNextPage();
            Toast.makeText(VideoResultListActivity.this, "No Results found", Toast.LENGTH_LONG).show();
            return;
        }
        //update data store
        if(results.data != null && results.data.size() != 0) {
            mDataFragment.addData(results.data);
        }
        //check if there are more pages to load
        if(results.paging == null ||
                results.paging.next == null ||
                results.paging.next.length() == 0) {
            mDataFragment.setLoadComplete();
            mDataFragment.resetNextPage();
        } else {
            //parse the next page number
            Log.d(TAG, "paging next: " + results.paging.next);
            String[] next = results.paging.next.split("[&\\?]");
            //Log.d(TAG, "tags: "+ Arrays.toString(next));
            Integer nextPage = 0;
            for(String n: next) {
                if(n.startsWith("page")) {
                    nextPage= Integer.parseInt(n.substring(n.indexOf("page=")+5));
                    break;
                }
            }
            Log.d(TAG, "next page: " + nextPage);
            if(nextPage == 0) {
                mDataFragment.setLoadComplete();
                mDataFragment.resetNextPage();
            }
            mDataFragment.setNextPage(nextPage);
        }
        mAdapter.notifyDataSetChanged();
        Log.d(TAG, "result size: " + mDataFragment.getData().size());
    }

    /**
     * checks whether more data can be fetched.
     * @return
     */
    private boolean canFetchMore() {
        Log.d(TAG, "mIsLoadComplete: " + mDataFragment.isLoadComplete() + " " + " is_running: " + mQs.isRunning());
        return !mDataFragment.isLoadComplete() && !mQs.isRunning();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchMenuItem = menu.findItem( R.id.search );
        //searchMenuItem.expandActionView();
        //MenuItemCompat.expandActionView(searchMenuItem);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isMainProgressBarVisible()) {
            outState.putBoolean(IS_MAIN_PROGRESS_VISIBLE, true);
        }
    }
}
