package com.smartmaggie.movies;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MovieDetails extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null){

            Bundle arguments = new Bundle();
            MovieDetailsFragment fragment = new MovieDetailsFragment();

            Intent i = getIntent();
            arguments.putString("url", i.getExtras().getString("url"));
            arguments.putString("title", i.getExtras().getString("title"));
            arguments.putInt("rating", i.getExtras().getInt("rating"));
            arguments.putString("release_date", i.getExtras().getString("release_date"));
            arguments.putString("overview", i.getExtras().getString("overview"));
            arguments.putInt("popularity", i.getExtras().getInt("popularity"));
            arguments.putInt("id", i.getExtras().getInt("id"));
            arguments.putBoolean("favOnly", i.getExtras().getBoolean("favOnly"));
            Log.e("movie id in md=", i.getExtras().getInt("id") + "");

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home){
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }

        if (id == R.id.sharing) {
            MovieDetailsFragment fragment = (MovieDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
            fragment.shareLink();
        }

        return super.onOptionsItemSelected(item);
    }
}
