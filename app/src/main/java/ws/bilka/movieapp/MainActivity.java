package ws.bilka.movieapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ws.bilka.movieapp.adapter.NowPlayingAdapter;
import ws.bilka.movieapp.model.NowPlayingItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mListView;
    private NowPlayingAdapter mNowPlayingAdapter;
    private List<NowPlayingItem> mNowPlayingItems;

    private int cursor = 1;
    private boolean loadingInProgress = false;

    String nowPlayingItemUrlImagePoster;
    Button mNowPlayingBtn, mPopularBtn, mUpcomingBtn, mTopRatedBtn;
    String something = "now_playing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNowPlayingBtn = (Button)findViewById(R.id.now_playing);
        mNowPlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNowPlayingItems.clear();
                mNowPlayingAdapter.notifyDataSetChanged();
                cursor = 1;
                something = "now_playing";
                String mMovieTask = Constants.MOVIE_URL + Constants.PART_URL + something + Constants.API_KEY + Constants.LANGUAGE;
                new NowPlayingMovieTask(mMovieTask).execute();
            }
        });

        mPopularBtn = (Button)findViewById(R.id.popular);
        mPopularBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNowPlayingItems.clear();
                mNowPlayingAdapter.notifyDataSetChanged();
                something = "popular";
                cursor = 1;
                new NowPlayingMovieTask(Constants.MOVIE_URL + Constants.PART_URL + something + Constants.API_KEY + Constants.LANGUAGE).execute();
            }
        });

        mUpcomingBtn = (Button)findViewById(R.id.upcoming);
        mUpcomingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNowPlayingItems.clear();
                mNowPlayingAdapter.notifyDataSetChanged();
                cursor = 1;
                something = "upcoming";
                new NowPlayingMovieTask(Constants.MOVIE_URL + Constants.PART_URL + something + Constants.API_KEY + Constants.LANGUAGE).execute();
            }
        });

        mTopRatedBtn = (Button)findViewById(R.id.top_rated);
        mTopRatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNowPlayingItems.clear();
                mNowPlayingAdapter.notifyDataSetChanged();
                cursor = 1;
                something = "top_rated";
                new NowPlayingMovieTask(Constants.MOVIE_URL + Constants.PART_URL + something + Constants.API_KEY + Constants.LANGUAGE).execute();
            }
        });

        mNowPlayingItems = new ArrayList<NowPlayingItem>();

        mListView = (ListView)findViewById(R.id.listViewNowPlaying);
        mNowPlayingAdapter = new NowPlayingAdapter(this, mNowPlayingItems);
        mListView.setAdapter(mNowPlayingAdapter);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView listView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= listView.getCount() - 1 - 1) {
                        cursor += 1;
                        new NowPlayingMovieTask(Constants.MOVIE_URL + Constants.PART_URL + something + Constants.API_KEY + Constants.LANGUAGE + "&page=" + cursor).execute();
                        loadingInProgress = true;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        new NowPlayingMovieTask(Constants.MOVIE_URL + Constants.PART_URL + something + Constants.API_KEY + Constants.LANGUAGE).execute();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NowPlayingItem item = mNowPlayingItems.get(position);
                Intent detailIntent = new Intent(MainActivity.this, DetailsMovieActivity.class);
                detailIntent.putExtra("movieId", item.getMovieId());
                startActivity(detailIntent);
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

        ImageButton searchBtn = (ImageButton) findViewById(R.id.search_button);
        if (searchBtn != null) {
            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                }
            });
        }
    }

    class NowPlayingMovieTask extends AsyncTask<Void, Void, String> {
        private String url;

        public NowPlayingMovieTask(String url) {
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
                JSONArray results = dataJsonObj.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject movieObject = results.getJSONObject(i);
                    NowPlayingItem nowPlayingItem = new NowPlayingItem();

                    String movie = movieObject.getString("id");
                    nowPlayingItem.setMovieId(movie);

                    nowPlayingItemUrlImagePoster = movieObject.getString("backdrop_path");
                    nowPlayingItem.setNowPlayingImage(nowPlayingItemUrlImagePoster);

                    String titleMovie = movieObject.getString("title");
                    nowPlayingItem.setNowPlayingTitle(titleMovie);

                    String popularity = movieObject.getString("vote_average");
                    nowPlayingItem.setNowPlayingRating(popularity);

                    String overview = movieObject.getString("overview");
                    nowPlayingItem.setNowPlayingOverview(overview);

                    String posterPath = movieObject.getString("poster_path");
                    nowPlayingItem.setNowPlayingPoster(posterPath);

                    String releaseDate = movieObject.getString("release_date");
                    nowPlayingItem.setNowPlayingYear(releaseDate);

                    mNowPlayingItems.add(nowPlayingItem);
                }

                mNowPlayingAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
