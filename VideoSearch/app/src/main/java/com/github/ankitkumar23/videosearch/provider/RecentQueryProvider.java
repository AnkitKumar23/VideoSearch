package com.github.ankitkumar23.videosearch.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * provides search history suggestions
 * Created by AnkitKumar23 on 11/8/2015.
 */
public class RecentQueryProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.github.ankitkumar23.videosearch.provider.RecentQueryProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;
    public RecentQueryProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
