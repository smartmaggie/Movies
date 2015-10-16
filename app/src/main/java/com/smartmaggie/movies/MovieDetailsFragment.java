package com.smartmaggie.movies;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    TheMovie theMovie = null;
    Button button = null;
    ArrayAdapter<String> adapter = null;
    ArrayAdapter<String> adapter2 = null;
    ArrayList<String> numbers = new ArrayList<>();
    boolean inDb = false;
    boolean favOnly = false;
    boolean movieSelected = false;

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movieSelected = true;
            theMovie = new TheMovie();
            theMovie.id = getArguments().getInt("id");
            theMovie.url = getArguments().getString("url");
            theMovie.title = getArguments().getString("title");
            theMovie.rating = getArguments().getInt("rating");
            theMovie.release_date = getArguments().getString("release_date");
            theMovie.overview = getArguments().getString("overview");
            theMovie.popularity = getArguments().getInt("popularity");
            favOnly = getArguments().getBoolean("favOnly");
            Log.e("id in mdfrag=",getArguments().getInt("id") + "");
        } else {
            movieSelected = false;
            theMovie = new TheMovie();
            theMovie.id = 1;
            theMovie.url = "abcd";
            theMovie.title = "Please select a movie.";
            theMovie.rating = 0;
            theMovie.release_date = "    ";
            theMovie.overview = " ";
            theMovie.popularity = 0;
            favOnly = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_movie_details, container, false);


        ImageView imageView = (ImageView) contentView.findViewById(R.id.thumbnail);
        TextView tview = (TextView) contentView.findViewById(R.id.title);
        TextView rview = (TextView) contentView.findViewById(R.id.rating);
        TextView dview = (TextView) contentView.findViewById(R.id.release_date);
        TextView oview = (TextView) contentView.findViewById(R.id.synopsis);
        button = (Button) contentView.findViewById(R.id.favorite);
        View details = (View) contentView.findViewById(R.id.details);

        if (movieSelected) {
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w154/" + theMovie.url).into(imageView);
            tview.setText(theMovie.title);
            rview.setText(theMovie.rating + "/10");
            dview.setText(theMovie.release_date.substring(0, 4));
            oview.setText(theMovie.overview);
        } else {
            details.setVisibility(View.INVISIBLE);

            Toast.makeText(getActivity(),"Please select a movie.",Toast.LENGTH_LONG).show();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsFavorite(v);
            }
        });

        MovieOps movieOps = new MovieOps(getActivity());
        int rows = movieOps.getMovieCountById(theMovie.id);

        if (rows > 0) {
            theMovie.favorite = movieOps.getMovieById(theMovie.id).favorite;
            if (theMovie.favorite) {
                button.setText(" Unmark as \nFavorite");
            }
            inDb = true;
        } else {
            button.setText(" Mark as \nFavorite");
        }

        adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.trailer_item,
                R.id.display,
                numbers
        );

        adapter2 = new ArrayAdapter<String>(
                getActivity(),
                R.layout.review_item,
                R.id.review_content,
                theMovie.reviews
        );

        final ListView listView = (ListView) contentView.findViewById(R.id.trailers_listview);
        ListView listView2 = (ListView) contentView.findViewById(R.id.reviews_listview);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Object item = listView.getItemAtPosition(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) theMovie.trailers.get(position)));
                startActivity(browserIntent);
            }
        });

        setListViewHeightBasedOnChildren(listView);
        setListViewHeightBasedOnChildren(listView2);

        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);

        if(movieSelected) {
            if (!favOnly) {
                new FetchTrailersTask().execute(new Integer(theMovie.id));
                new FetchReviewsTask().execute(new Integer(theMovie.id));
            } else {
                theMovie = movieOps.getMovieById(theMovie.id);

                adapter.clear();
                for (int j = 1; j <= theMovie.trailers.size(); j++) {
                    adapter.add("TRAILER " + j);
                }
                adapter.notifyDataSetChanged();

                adapter2.clear();
                for (int j = 0; j < theMovie.reviews.size(); j++) {
                    adapter2.add(theMovie.reviews.get(j));
                }
                adapter2.notifyDataSetChanged();
            }
        }

        return contentView;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, RadioGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void markAsFavorite(View view) {
        final Context context = getActivity();
        final int duration = Toast.LENGTH_SHORT;
        MovieOps movieOps = new MovieOps(getActivity());

        if (theMovie.favorite) {
            theMovie.favorite = false;
            movieOps.delete(theMovie.id);
            button.setText(" Mark as \nFavorite");
            Toast.makeText(context, "Unmarked as Favorite!", duration).show();
        } else {
            theMovie.favorite = true;
            movieOps.insert(theMovie);
            button.setText(" Unmark as \nFavorite");
            Toast.makeText(context, "Marked as Favorite!", duration).show();
        }
    }

    public void shareLink(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer for " + theMovie.title + "!\n" + theMovie.trailers.get(0));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public class FetchTrailersTask extends AsyncTask<Integer, Void, ArrayList> {

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            if (arrayList != null) {
                adapter.clear();

                for (int j = 1; j <= arrayList.size(); j++) {
                    theMovie.trailers.add((String) arrayList.get(j - 1));
                    adapter.add("TRAILER " + j);
                }
            }
        }

        private ArrayList<String> getKeysFromJson(String trailerJsonStr) throws JSONException {
            JSONObject obj = new JSONObject(trailerJsonStr);
            JSONArray arr = obj.getJSONArray("results");
            ArrayList<String> keys = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject movie = arr.getJSONObject(i);
                keys.add("http://www.youtube.com/watch?v=" + movie.getString("key"));
            }
            return keys;
        }

        @Override
        protected ArrayList doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailerLinks = null;
            ArrayList<String> trailerKeys = null;

            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=" + getString(R.string.api_key));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    trailerLinks = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    trailerLinks = null;
                }
                trailerLinks = buffer.toString();
                trailerKeys = getKeysFromJson(trailerLinks);
            } catch (Exception e) {
                Log.e("Error", e.toString());
                trailerLinks = null;
            } finally {
                if (urlConnection != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return trailerKeys;
        }
    }

    public class FetchReviewsTask extends AsyncTask<Integer, Void, ArrayList> {

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            if (arrayList != null) {
                adapter2.clear();
                for (Object content : arrayList) {
                    adapter2.add((String) content);
                }
            }
        }

        private ArrayList<String> getReviewsFromJson(String reviewJsonStr) throws JSONException {
            JSONObject obj = new JSONObject(reviewJsonStr);
            JSONArray arr = obj.getJSONArray("results");
            ArrayList<String> contents = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject movie = arr.getJSONObject(i);
                contents.add(movie.getString("content"));
            }
            return contents;
        }

        @Override
        protected ArrayList doInBackground(Integer... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviews = null;
            ArrayList<String> reviewContent = null;

            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=" + getString(R.string.api_key));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    reviews = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    reviews = null;
                }
                reviews = buffer.toString();
                Log.e("Reviews", reviews);
                reviewContent = getReviewsFromJson(reviews);
            } catch (Exception e) {
                Log.e("Error", e.toString());
                reviews = null;
            } finally {
                if (urlConnection != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return reviewContent;
        }
    }
}
