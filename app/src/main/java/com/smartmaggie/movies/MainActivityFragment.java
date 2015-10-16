package com.smartmaggie.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import android.view.Menu;
import android.view.MenuItem;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static boolean favOnly = false;
    private static Callbacks mCallbacksDummy = new Callbacks() {
        @Override
        public void onItemSelected(int position) {
            Log.e("Dummy", "whatever blah blah blah");
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = mCallbacksDummy;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallbacks = (Callbacks) activity;
    }

    Callbacks mCallbacks = mCallbacksDummy;

    public static ImageAdapter imageAdapter;
    public static ArrayList<TheMovie> movies = new ArrayList<TheMovie>();

    public MainActivityFragment() {
    }

    public interface Callbacks{
        public void onItemSelected(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) contentView.findViewById(R.id.movie_grid);

        imageAdapter = new ImageAdapter(getActivity());
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onItemSelected(position);
            }
        });

        if (!favOnly) {
            new FetchPostersTask().execute("popularity");

        } else {
            MovieOps movieOps = new MovieOps(getActivity());
            movies = movieOps.getMovieList();
        }



        //return inflater.inflate(R.layout.fragment_main, container, false);
        return contentView;
    }

    public void sortBy(String arg){
        if (favOnly) {
            MovieOps movieOps = new MovieOps(getActivity());
            movies = movieOps.getMovieList();
            if (arg.equals("vote_average")) {
                Collections.sort(movies, TheMovie.RatingComparator);
            } else {
                Collections.sort(movies, TheMovie.PopularityComparator);
            }
        } else {
            new FetchPostersTask().execute(arg);
        }
        imageAdapter.notifyDataSetChanged();
    }

    public void doFavorite(MenuItem item){
        Log.e("doFav", "doFav called");
        if (item.isChecked()) {
            favOnly = true;
            MovieOps movieOps = new MovieOps(getActivity());
            movies = movieOps.getMovieList();
            //imageAdapter.notifyDataSetChanged();
        } else {
            favOnly = false;
            new FetchPostersTask().execute("popularity");
        }
        imageAdapter.notifyDataSetChanged();
    }

    public class ImageAdapter extends BaseAdapter {
        private Activity mContext;

        public ImageAdapter(Activity c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return movies.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setAdjustViewBounds(true);
            Picasso.with(this.mContext).load("http://image.tmdb.org/t/p/w185/" + movies.get(position).url).into(imageView);
            return imageView;
        }

    }

    public class FetchPostersTask extends AsyncTask<String, Void, ArrayList<TheMovie>> {

        @Override
        protected void onPostExecute(ArrayList<TheMovie> themovies) {
            movies = themovies;
            imageAdapter.notifyDataSetChanged();
            if(movies.size()==0){
                Toast.makeText(getActivity(), "No movies retrieved. Maybe network issue?", Toast.LENGTH_SHORT).show();
            }
        }

        private ArrayList getUrlsFromJson(String JSONposter) throws JSONException {
            JSONObject obj = new JSONObject(JSONposter);
            JSONArray arr = obj.getJSONArray("results");
            ArrayList<TheMovie> themovies = new ArrayList<TheMovie>();

            for (int i = 0; i < arr.length(); i++) {

                TheMovie themovie = new TheMovie();
                JSONObject movie = arr.getJSONObject(i);
                themovie.url = movie.getString("poster_path");
                themovie.rating = movie.getInt("vote_average");
                themovie.release_date = movie.getString("release_date");
                themovie.title = movie.getString("title");
                themovie.overview = movie.getString("overview");
                themovie.popularity = movie.getInt("popularity");
                themovie.id = movie.getInt("id");
                themovies.add(themovie);
            }
            return themovies;
        }

        @Override
        protected ArrayList<TheMovie> doInBackground(String... params) {
            Log.e("Here is OK", "doInBackground called.");
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String posterUrl = null;
            ArrayList<TheMovie> theMovies = null;


            try {
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=" + params[0] + ".desc&api_key=" + getString(R.string.api_key));

                Log.e("URL", url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                Log.e("Response code:",urlConnection.getResponseCode() + "");

                Log.e("connected?", "connected");

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    posterUrl = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    posterUrl = null;
                }
                posterUrl = buffer.toString();
                theMovies = getUrlsFromJson(posterUrl);
                Log.e("Got URLs", posterUrl);
                reader.close();
            } catch (Exception e) {
                Log.e("Error", e.toString());
                posterUrl = null;
                theMovies = new ArrayList<>();
            } finally {
                /*if (urlConnection != null) {
                    try {
                       reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
            }
            return theMovies;
        }
    }
}
