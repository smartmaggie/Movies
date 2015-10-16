package com.smartmaggie.movies;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;

public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callbacks{

    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailsFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (!item.isChecked()) {
            item.setChecked(true);
        } else {
            item.setChecked(false);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_sort){
            MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.movie_grid);
            fragment.sortBy("vote_average");
        }

        if (id == R.id.action_sort2){
            MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.movie_grid);
            fragment.sortBy("popularity");
        }

        if (id == R.id.favorites){
            MainActivityFragment fragment = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.movie_grid);
            fragment.doFavorite(item);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(int position) {

        if (mTwoPane){
            Bundle arguments = new Bundle();
            MovieDetailsFragment fragment = new MovieDetailsFragment();

            arguments.putString("url", MainActivityFragment.movies.get(position).url);
            arguments.putString("title", MainActivityFragment.movies.get(position).title);
            arguments.putInt("rating", MainActivityFragment.movies.get(position).rating);
            arguments.putString("release_date", MainActivityFragment.movies.get(position).release_date);
            arguments.putString("overview", MainActivityFragment.movies.get(position).overview);
            arguments.putInt("popularity", MainActivityFragment.movies.get(position).popularity);
            arguments.putInt("id", MainActivityFragment.movies.get(position).id);
            arguments.putBoolean("favOnly", MainActivityFragment.favOnly);

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent i = new Intent(this, MovieDetails.class);
            i.putExtra("url", MainActivityFragment.movies.get(position).url);
            i.putExtra("title", MainActivityFragment.movies.get(position).title);
            i.putExtra("rating", MainActivityFragment.movies.get(position).rating);
            i.putExtra("release_date", MainActivityFragment.movies.get(position).release_date);
            i.putExtra("overview", MainActivityFragment.movies.get(position).overview);
            i.putExtra("popularity", MainActivityFragment.movies.get(position).popularity);
            i.putExtra("id", MainActivityFragment.movies.get(position).id);
            i.putExtra("favOnly", MainActivityFragment.favOnly);
            Log.e("movie id=", MainActivityFragment.movies.get(position).id + "");
            startActivity(i);
        }
    }
}
