package com.smartmaggie.movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieOps {
    private DBHelper dbHelper;

    public MovieOps(Context context) {
        dbHelper = new DBHelper(context);
    }

    public int insert(TheMovie theMovie) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", theMovie.id);
        values.put("favorite", theMovie.favorite);
        values.put("url", theMovie.url);
        values.put("title", theMovie.title);
        values.put("rating", theMovie.rating);
        values.put("release_date", theMovie.release_date);
        values.put("overview", theMovie.overview);
        values.put("popularity", theMovie.popularity);

        long movie_Id = db.insert("movie", null, values);

        for (int i = 0; i < theMovie.trailers.size(); i++) {
            ContentValues values2 = new ContentValues();
            values2.put("movie_id", theMovie.id);
            values2.put("url", theMovie.trailers.get(i));
            db.insert("trailer", null, values2);
        }

        for (int i = 0; i < theMovie.reviews.size(); i++) {
            ContentValues values2 = new ContentValues();
            values2.put("movie_id", theMovie.id);
            values2.put("review", theMovie.reviews.get(i));
            db.insert("review", null, values2);
        }

        db.close();
        return (int) movie_Id;
    }

    public void delete(int movie_Id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("movie", "id" + "= ?", new String[]{String.valueOf(movie_Id)});
        db.close();
    }

    public void update(TheMovie theMovie) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", theMovie.id);
        values.put("favorite", theMovie.favorite);
        values.put("url", theMovie.url);
        values.put("title", theMovie.title);
        values.put("rating", theMovie.rating);
        values.put("release_date", theMovie.release_date);
        values.put("overview", theMovie.overview);
        values.put("popularity", theMovie.popularity);

        db.update("movie", values, "id" + "= ?", new String[]{String.valueOf(theMovie.id)});
        db.close();
    }

    public ArrayList<TheMovie> getMovieList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM movie";

        /*if (by == 1) {
            selectQuery += "rating";
        } else {
            selectQuery += "popularity";
        }
        selectQuery += " desc";*/

        ArrayList<TheMovie> movieList = new ArrayList<TheMovie>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TheMovie movie = new TheMovie();
                movie.id = cursor.getInt(cursor.getColumnIndex("id"));
                movie.favorite = cursor.getString(cursor.getColumnIndex("favorite")).equals("1");
                movie.url = cursor.getString(cursor.getColumnIndex("url"));
                movie.title = cursor.getString(cursor.getColumnIndex("title"));
                movie.rating = cursor.getInt(cursor.getColumnIndex("rating"));
                movie.release_date = cursor.getString(cursor.getColumnIndex("release_date"));
                movie.overview = cursor.getString(cursor.getColumnIndex("overview"));
                movie.popularity = cursor.getInt(cursor.getColumnIndex("popularity"));
                movieList.add(movie);

                Log.e("movie id="+movie.id, "rating="+movie.rating);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return movieList;
    }

    public TheMovie getMovieById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM movie WHERE id =?";

        TheMovie theMovie = new TheMovie();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            do {
                theMovie.id = cursor.getInt(cursor.getColumnIndex("id"));
                theMovie.favorite = cursor.getString(cursor.getColumnIndex("favorite")).equals("1");
                theMovie.url = cursor.getString(cursor.getColumnIndex("url"));
                theMovie.title = cursor.getString(cursor.getColumnIndex("title"));
                theMovie.rating = cursor.getInt(cursor.getColumnIndex("rating"));
                theMovie.release_date = cursor.getString(cursor.getColumnIndex("release_date"));
                theMovie.overview = cursor.getString(cursor.getColumnIndex("overview"));
                theMovie.popularity = cursor.getInt(cursor.getColumnIndex("popularity"));
            } while (cursor.moveToNext());
        }

        cursor.close();

        selectQuery = "SELECT * FROM trailer WHERE movie_id = " + id;

        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String url = cursor.getString(cursor.getColumnIndex("url"));
                theMovie.trailers.add(url);
                Log.e("Movie trailer: ", id + ": " + url);
            } while (cursor.moveToNext());
        }

        cursor.close();

        selectQuery = "SELECT * FROM review WHERE movie_id = " + id;

        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String review = cursor.getString(cursor.getColumnIndex("review"));
                theMovie.reviews.add(review);
                Log.e("Movie review: ", id + ": " + review);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return theMovie;
    }

    public int getMovieCountById(int id) {
        int rows = 0;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT count(*) as rows from movie where id =?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            do {
                rows = cursor.getInt(cursor.getColumnIndex("rows"));
            } while (cursor.moveToNext());
        }

        return rows;
    }

    /* test purpose */
    public ArrayList<String> getTrailerList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM trailer";

        ArrayList<String> trailerList = new ArrayList<String>();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {


                trailerList.add(cursor.getString(cursor.getColumnIndex("url")));


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return trailerList;
    }
}

