package com.smartmaggie.movies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "movie.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MOVIE = "create table movie (id integer primary key, favorite int," +
                "url text, title text, rating int, release_date text, overview text, popularity int)";
        String CREATE_TABLE_TRAILER = "create table trailer (id int primary key, url text," +
                "movie_id int, foreign key (movie_id) references movie(id) on delete cascade)";
        String CREATE_TABLE_REVIEW = "create table review(id int primary key, review text," +
                "movie_id int, foreign key (movie_id) references movie(id) on delete cascade)";
        db.execSQL(CREATE_TABLE_MOVIE);
        db.execSQL(CREATE_TABLE_TRAILER);
        db.execSQL(CREATE_TABLE_REVIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists trailer");
        db.execSQL("drop table if exists review");
        db.execSQL("drop table if exists movie");
        onCreate(db);
    }
}
