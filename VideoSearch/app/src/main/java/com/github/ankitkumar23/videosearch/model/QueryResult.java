package com.github.ankitkumar23.videosearch.model;

import java.util.List;

/**
 * Structure of the JSON Result item returned by
 * Vimeo search
 * Created by AnkitKumar23 on 10/23/2015.
 */
public class QueryResult {
    public int total;
    public int page;
    public int per_page;

    public Paging paging;
    public List<Result> data;

    public class Paging {
        public String next;
        public String previous;
        public String first;
        public String last;
    }

    public class Result {
        public String uri;
        public String name;
        public String description;
        public long duration;
        public String created_time;
        public List<String> content_rating;
        public Pictures pictures;
        public Stats stats;
        public Metadata metadata;
        public User user;
    }

    public class User {
        public String name;
        public Pictures pictures;
    }

    public class Metadata {
        public Connections connections;
    }

    public class Connections {
        public Likes likes;
    }

    public class Likes {
        public long total;
    }

    public class Pictures {
        public List<Sizes> sizes;
    }

    public class Sizes {
        public int width;
        public int height;
        public String link;
    }

    public class Stats {
        public long plays;
    }
}
