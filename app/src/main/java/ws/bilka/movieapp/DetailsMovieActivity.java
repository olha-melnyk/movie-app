package ws.bilka.movieapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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

import it.sephiroth.android.library.picasso.MemoryPolicy;
import it.sephiroth.android.library.picasso.Picasso;

public class DetailsMovieActivity extends AppCompatActivity {

    private static final String TAG = DetailsMovieActivity.class.getSimpleName();
    String movieId;
    DynamicImageView imageDetail;
    TextView titleDetail, ratingDetail, overviewDetail, releaseDetail, productionCountriesDetail,
            productionCompaniesDetail, genresDetail, runtimeDetail, actorsNameDetail;

    ProgressBar progressBar;
    LinearLayout linearLayout;
    ScrollView scrollView;

    String[] genresArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_movie);
        movieId = getIntent().getStringExtra("movieId");

        progressBar = (ProgressBar) findViewById(R.id.progress);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        imageDetail = (DynamicImageView)findViewById(R.id.imageDetail);
        titleDetail = (TextView)findViewById(R.id.titleDetail);
        ratingDetail = (TextView)findViewById(R.id.ratingDetail);
        overviewDetail = (TextView)findViewById(R.id.overviewDetail);
        releaseDetail = (TextView)findViewById(R.id.releaseDetail);
        productionCountriesDetail = (TextView)findViewById(R.id.productionDetail);
        productionCompaniesDetail = (TextView)findViewById(R.id.productionCompanies);
        genresDetail = (TextView)findViewById(R.id.genresDetail);
        runtimeDetail = (TextView)findViewById(R.id.runtimeDetail);
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);

        new DetailsMovieTask("https://api.themoviedb.org/3/movie/" + movieId + Constants.API_KEY + Constants.LANGUAGE).execute();

    }

    class DetailsMovieTask extends AsyncTask<Void, Void, String> {
        private String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            imageDetail.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }

        public DetailsMovieTask(String url) {
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
                JSONArray genres = dataJsonObj.getJSONArray("genres");
                genresArray = new String[genres.length()];
                for (int i = 0; i < genres.length(); i++) {
                    JSONObject genresObject = genres.getJSONObject(i);
                    String name = genresObject.getString("name");
                    genresArray[i] = name;

                    genresDetail.append(genresArray[i]);
                    genresDetail.append(", ");
                }

                String poster = dataJsonObj.getString("backdrop_path");
                Log.i(TAG, "https://image.tmdb.org/t/p/w500_and_h281_bestv2" + poster);
                Picasso.with(DetailsMovieActivity.this)
                        .load("https://image.tmdb.org/t/p/w500_and_h281_bestv2/" + poster)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(imageDetail);

                String title = dataJsonObj.getString("title");
                titleDetail.setText(title);

                String date = dataJsonObj.getString("release_date");
                releaseDetail.setText(date);

                String rating = dataJsonObj.getString("vote_average");
                ratingDetail.setText(rating);

                String overview = dataJsonObj.getString("overview");
                overviewDetail.setText(overview);

                String runtime = dataJsonObj.getString("runtime");
                runtimeDetail.setText(runtime);

                JSONArray countryProduction = dataJsonObj.getJSONArray("production_countries");
                for (int i = 0; i < countryProduction.length(); i++) {
                    JSONObject countryProductionJSONObject = countryProduction.getJSONObject(i);
                    String country = countryProductionJSONObject.getString("name");
                    productionCountriesDetail.setText(country);
                }

                JSONArray companiesProduction = dataJsonObj.getJSONArray("production_companies");
                for (int i = 0; i < companiesProduction.length(); i++) {
                    JSONObject companiesProductionJSONObject = companiesProduction.getJSONObject(i);
                    String companies = companiesProductionJSONObject.getString("name");
                    productionCompaniesDetail.setText(companies);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
            imageDetail.setVisibility(View.VISIBLE);
        }
    }
}
