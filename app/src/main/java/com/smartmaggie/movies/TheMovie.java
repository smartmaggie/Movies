package com.smartmaggie.movies;

import java.util.ArrayList;
import java.util.Comparator;

public class TheMovie {
    public String url;
    public String title;
    public int rating;
    public String release_date;
    public String overview;
    public int popularity;
    public int id;
    public boolean favorite = false;
    public ArrayList<String> trailers = new ArrayList<>();
    public ArrayList<String> reviews = new ArrayList<>();

    public static Comparator<TheMovie> RatingComparator = new Comparator<TheMovie>() {
        @Override
        public int compare(TheMovie m1, TheMovie m2) {
            return m2.rating - m1.rating;
        }
    };

    public static Comparator<TheMovie> PopularityComparator = new Comparator<TheMovie>() {
        @Override
        public int compare(TheMovie m1, TheMovie m2) {
            return m2.popularity - m1.popularity;
        }
    };
}

