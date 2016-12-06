package ws.bilka.movieapp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ws.bilka.movieapp.adapter.NowPlayingAdapter;
import ws.bilka.movieapp.model.NowPlayingItem;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = SearchActivity.class.getSimpleName();

    NowPlayingAdapter mSearchAdapter;
    private List<NowPlayingItem> mNowPlayingItems;

    Spinner mSpinnerYears;
    Spinner mSpinnerSort;
    Spinner mSpinnerGenre;

    String[] sort = {"popularity.desc", "popularity.asc", "vote_average.desc", "vote_average.asc", "release_date.desc", "release_date.asc"};
    String[] genreIds;
    String[] genre;

    String selectGenre;
    String selectYear;
    String selectSortBy;
    private int cursor = 1;

    ListView mListView;
    ProgressBar progressBar;
    LinearLayout linearLayoutToolbar;
    LinearLayout linearLayout;
    LinearLayout linearLayoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        linearLayoutToolbar = (LinearLayout)findViewById(R.id.linearLayout_toolbar);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        linearLayoutButton = (LinearLayout)findViewById(R.id.linearLayout_button);

        new GenreMovieTask().execute();

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1900; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        mSpinnerYears = (Spinner)findViewById(R.id.spinner_year);
        assert mSpinnerYears != null;
        mSpinnerYears.setAdapter(adapter);
        mSpinnerYears.setSelection(115);
        mSpinnerYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectYear = mSpinnerYears.getSelectedItem().toString();
                mNowPlayingItems.clear();
                mSearchAdapter.notifyDataSetChanged();
                new RetrieveMovieTask(Constants.MOVIE_URL + "/3/discover/movie" + Constants.API_KEY +
                        "&language=en-US&sort_by=" + selectSortBy + "&include_adult=false&include_video=true&page="
                        + cursor  + "&with_genres=" + selectGenre + "&primary_release_year=" + selectYear).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ArrayAdapter<String> adapterSort = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sort);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSort = (Spinner)findViewById(R.id.spinner_sort);
        assert mSpinnerSort != null;
        mSpinnerSort.setAdapter(adapterSort);
        mSpinnerSort.setPrompt("Title");
        mSpinnerSort.setSelection(0);
        mSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectSortBy = mSpinnerSort.getSelectedItem().toString();
                mNowPlayingItems.clear();
                mSearchAdapter.notifyDataSetChanged();
                new RetrieveMovieTask(Constants.MOVIE_URL + "/3/discover/movie" + Constants.API_KEY +
                        "&language=en-US&sort_by=" + selectSortBy + "&include_adult=false&include_video=true&page="
                        + cursor + "&with_genres=" + selectGenre + "&primary_release_year=" + selectYear).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mSpinnerGenre = (Spinner) findViewById(R.id.spinner);
        mSpinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selectGenre = genreIds[position];
                mNowPlayingItems.clear();
                mSearchAdapter.notifyDataSetChanged();
                new RetrieveMovieTask(Constants.MOVIE_URL + "/3/discover/movie" + Constants.API_KEY +
                        "&language=en-US&sort_by=" + selectSortBy + "&include_adult=false&include_video=true&page="
                        + cursor  + "&with_genres=" + selectGenre + "&primary_release_year=" + selectYear).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ImageButton homeBtn = (ImageButton) findViewById(R.id.home_button);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent homeIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(homeIntent);
                }
            });
        }

        mNowPlayingItems = new ArrayList<NowPlayingItem>();

        mListView = (ListView)findViewById(R.id.listView);
        mSearchAdapter = new NowPlayingAdapter(this, mNowPlayingItems);
        mListView.setAdapter(mSearchAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView listView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= listView.getCount() - 1 - 1) {
                        cursor += 1;
                        new RetrieveMovieTask(Constants.MOVIE_URL + "/3/discover/movie" + Constants.API_KEY +
                                "&language=en-US&sort_by=" + selectSortBy + "&include_adult=false&include_video=true&page="
                                + cursor + "&with_genres=" + selectGenre + "&primary_release_year=" + selectYear).execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NowPlayingItem item = mNowPlayingItems.get(position);
                Intent detailIntent = new Intent(SearchActivity.this, DetailsMovieActivity.class);
                detailIntent.putExtra("movieId", item.getMovieId());
                startActivity(detailIntent);
            }
        });
    }

    class RetrieveMovieTask extends AsyncTask<Void, Void, String> {

        private String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            linearLayoutButton.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            linearLayoutToolbar.setVisibility(View.GONE);
        }

        public RetrieveMovieTask(String url) {
            this.url = url;
        }


        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(this.url);
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
                JSONArray genres = dataJsonObj.getJSONArray("results");

                for (int i = 0; i < genres.length(); i++) {
                    JSONObject genresJSONObject = genres.getJSONObject(i);
                    NowPlayingItem nowPlayingItem = new NowPlayingItem();

                    String id = genresJSONObject.getString("id");
                    nowPlayingItem.setMovieId(id);

                    String title = genresJSONObject.getString("title");
                    nowPlayingItem.setNowPlayingTitle(title);

                    String vote = genresJSONObject.getString("vote_average");
                    nowPlayingItem.setNowPlayingRating(vote);

                    String backdrop = genresJSONObject.getString("backdrop_path");
                    nowPlayingItem.setNowPlayingImage(backdrop);

                    String poster = genresJSONObject.getString("poster_path");
                    nowPlayingItem.setNowPlayingPoster(poster);

                    String overview = genresJSONObject.getString("overview");
                    nowPlayingItem.setNowPlayingOverview(overview);

                    String releaseDate = genresJSONObject.getString("release_date");
                    nowPlayingItem.setNowPlayingYear(releaseDate);


                    mNowPlayingItems.add(nowPlayingItem);
                }
                mSearchAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            linearLayoutButton.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            linearLayoutToolbar.setVisibility(View.VISIBLE);
        }
    }

    class GenreMovieTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(Constants.MOVIE_URL + Constants.GENRE_URL + Constants.API_KEY + Constants.LANGUAGE);
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
                JSONArray genres = dataJsonObj.getJSONArray("genres");

                genre = new String[genres.length()];
                genreIds = new String[genres.length()];

                for (int i = 0; i < genres.length(); i++) {
                    JSONObject genresJSONObject = genres.getJSONObject(i);

                    String genreIdMovies = genresJSONObject.getString("id");
                    Log.i(TAG, "Id genres: " + genreIdMovies);

                    String genreName = genresJSONObject.getString("name");
                    Log.i(TAG, "Name: " + genreName);

                    genreIds[i] = genreIdMovies;
                    genre[i] = genreName;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_item, genre);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mSpinnerGenre.setAdapter(adapter);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}