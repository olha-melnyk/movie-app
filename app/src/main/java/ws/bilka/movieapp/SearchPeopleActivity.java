package ws.bilka.movieapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.MemoryPolicy;
import it.sephiroth.android.library.picasso.Picasso;
import ws.bilka.movieapp.adapter.GridViewPeopleMovieAdapter;
import ws.bilka.movieapp.model.ActorMovieItem;

public class SearchPeopleActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String GENRE_URL = "/3/person/";

    DynamicImageView photoPeople;
    TextView namePeople;
    TextView biographyPeople;
    TextView birthdayPeople;
    TextView placeOfBirthPeople;

    private ExpandableHeightGridView gridView;
    private GridViewPeopleMovieAdapter gridAdapter;
    private List<ActorMovieItem> actors;


    ProgressBar progressBar;
    ScrollView scrollView;
    String peopleMovieName;
    String peoplePhotoUrl;
    String castId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_people);

        peopleMovieName = getIntent().getStringExtra("peopleName").replace(" ", "%20");
        castId = getIntent().getStringExtra("actorId");
        Log.i(TAG, "Actors Id is: " + castId);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        photoPeople = (DynamicImageView)findViewById(R.id.photo);
        namePeople = (TextView)findViewById(R.id.name);
        biographyPeople = (TextView)findViewById(R.id.biography);
        birthdayPeople = (TextView)findViewById(R.id.birthday);
        placeOfBirthPeople = (TextView)findViewById(R.id.placeOfBirth);

        actors = new ArrayList<ActorMovieItem>();
        gridView = (ExpandableHeightGridView)findViewById(R.id.grid);
        gridAdapter = new GridViewPeopleMovieAdapter(this, actors);
        gridView.setAdapter(gridAdapter);
        gridView.setExpanded(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActorMovieItem actorItem = actors.get(position);
                Intent actorInfoIntent = new Intent(SearchPeopleActivity.this, DetailsMovieActivity.class);
                actorInfoIntent.putExtra("movieId", actorItem.getId());
                Picasso.with(getBaseContext()).invalidate(gridAdapter.toString());
                startActivity(actorInfoIntent);
            }
        });

        new RetrieveMovieTask().execute();
        new CreditsPersonMovieTask().execute();

    }

    class RetrieveMovieTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.MOVIE_URL + GENRE_URL + castId + Constants.API_KEY + Constants.LANGUAGE);
                Log.i(TAG, "URL: " + url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            if (strJson == null) {
                strJson = "THERE WAS AN ERROR";
            }
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(strJson);

                String name = dataJsonObj.getString("name");
                namePeople.setText(name);

                String birthday = dataJsonObj.getString("birthday");
                birthdayPeople.setText(birthday);

                String placeOfBirth = dataJsonObj.getString("place_of_birth");
                placeOfBirthPeople.setText(placeOfBirth);

                peoplePhotoUrl = dataJsonObj.getString("profile_path");
                Picasso.with(SearchPeopleActivity.this)
                        .load("https://image.tmdb.org/t/p/w300_and_h450_bestv2" + peoplePhotoUrl)
                        .placeholder(R.drawable.placeholder_photo)
                        .error(R.drawable.placeholder_photo).memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(photoPeople);

                String biography = dataJsonObj.getString("biography");
                biographyPeople.setText(biography);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    class CreditsPersonMovieTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.MOVIE_URL + GENRE_URL + castId + "/movie_credits" + Constants.API_KEY + Constants.LANGUAGE);
                Log.i(TAG, "URL: " + url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            if (strJson == null) {
                strJson = "THERE WAS AN ERROR";
            }
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray results = dataJsonObj.getJSONArray("cast");
                Log.i(TAG, "Cast: " + results);

                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieObject = results.getJSONObject(i);
                    ActorMovieItem actorItem = new ActorMovieItem();

                    String idCastPeople = movieObject.getString("id");
                    actorItem.setId(idCastPeople);

                    String knownForTitlePeople = movieObject.getString("title");
                    actorItem.setTitle(knownForTitlePeople);

                    String castPhoto = movieObject.getString("poster_path");
                    actorItem.setImage(castPhoto);

                    actors.add(actorItem);

                }
                gridAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }
}
