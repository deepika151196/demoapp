package com.seekho.demo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.seekho.demo.API.APIUtils;
import com.seekho.demo.Pojo.Movie;
import com.seekho.demo.Pojo.SubMovie;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private ImageView moviePoster, closeView;
    private TextView movieTitle, moviePlot, movieGenre, movieCast, movieEpisodes, movieRating, detailsNoDataView;
    private String animeId;
    private Movie movieDetail;
    private ProgressBar detailsLoader;
    private WebView youtubeTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.movie_details_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();

        Intent detailIntent = getIntent();
        if(detailIntent.hasExtra("anime_id")){
            animeId = detailIntent.getStringExtra("anime_id");
        }
        if(animeId == null || animeId.isEmpty()){
            return;
        }
        APIUtils.initializeRequestQueue(MovieDetailsActivity.this);
        requestAnimeDetails();
        init();
    }

    private void init(){
        try{
            closeView =  findViewById(R.id.close_view);
            closeView.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
            moviePoster = findViewById(R.id.movie_poster);
            youtubeTrailer = findViewById(R.id.youtube_video);
            movieTitle = findViewById(R.id.movie_title);
            moviePlot = findViewById(R.id.movie_plot);
            movieGenre = findViewById(R.id.movie_genre);
            movieCast = findViewById(R.id.movie_main_cast);
            movieEpisodes = findViewById(R.id.movie_number_of_episodes);
            movieRating = findViewById(R.id.movie_rating);
            detailsLoader = findViewById(R.id.details_loader);
            detailsLoader.setVisibility(VISIBLE);
            detailsNoDataView = findViewById(R.id.detail_no_data_view);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void requestAnimeDetails(){
        try {
            Response.Listener<JSONObject> response = response1 -> {
                if(response1 == null){
                    return;
                }
                try {
                    if(response1.has("data") && response1.getJSONObject("data").length() > 0){
                        JSONObject movieDetailJson = response1.getJSONObject("data");
                        movieDetail = new Movie(movieDetailJson.getString("mal_id")
                        , movieDetailJson.getString("title")
                        ,movieDetailJson.getString("episodes")
                        ,movieDetailJson.getString("rating")
                        ,movieDetailJson.getJSONObject("images").getJSONObject("jpg").getString("image_url")
                                ,movieDetailJson.getString("synopsis")
                        ,movieDetailJson.getJSONObject("trailer").getString("embed_url")
                        ,movieDetailJson.getJSONArray("genres")
                                ,movieDetailJson.getJSONArray("producers")
                                ,movieDetailJson.getJSONArray("studios"));
                        updateDetailsUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    updateDetailsUI();
                }
            };
            Response.ErrorListener errorListener = error -> {
                updateDetailsUI();
            };
            APIUtils.requestAnimeDetails(response,errorListener,animeId);
        } catch (Exception e) {
            e.printStackTrace();
            updateDetailsUI();
        }
    }

    private void updateDetailsUI(){
        try {
            detailsLoader.setVisibility(GONE);
            if(movieDetail != null){
                moviePoster.setVisibility(VISIBLE);
                movieTitle.setVisibility(VISIBLE);
                movieTitle.setVisibility(VISIBLE);
                moviePlot.setVisibility(VISIBLE);
                movieGenre.setVisibility(VISIBLE);
                if(movieDetail.getProducers() != null && movieDetail.getProducers().size() > 0){
                    movieCast.setVisibility(VISIBLE);
                } else {
                    movieCast.setVisibility(GONE);
                }
                if(movieDetail.getGenres() != null && movieDetail.getGenres().size() > 0){
                    movieGenre.setVisibility(VISIBLE);
                } else {
                    movieGenre.setVisibility(GONE);
                }
                movieEpisodes.setVisibility(VISIBLE);
                movieRating.setVisibility(VISIBLE);
                if(movieDetail.getYoutubeTrailerId() != null && !TextUtils.isEmpty(movieDetail.getYoutubeTrailerId())){
                    youtubeTrailer.setVisibility(VISIBLE);
                    moviePoster.setVisibility(GONE);
                } else {
                    youtubeTrailer.setVisibility(GONE);
                    moviePoster.setVisibility(VISIBLE);
                }
                setMovieDetailData();
            } else {
                moviePoster.setVisibility(GONE);
                movieTitle.setVisibility(GONE);
                movieTitle.setVisibility(GONE);
                moviePlot.setVisibility(GONE);
                movieGenre.setVisibility(GONE);
                movieCast.setVisibility(GONE);
                movieEpisodes.setVisibility(GONE);
                movieRating.setVisibility(GONE);
                youtubeTrailer.setVisibility(GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMovieDetailData(){
        try {
            if(movieDetail.getProducers() != null && movieDetail.getProducers().size() > 0) {
                String prodString = appendStrings(movieDetail.getProducers());
                if(prodString != null && !TextUtils.isEmpty(prodString)){
                    boldWithText("Producers : ", prodString, movieCast);
                }
            }
            if(movieDetail.getGenres() != null && movieDetail.getGenres().size() > 0) {
                String genreString = appendStrings(movieDetail.getGenres());
                if(genreString != null && !TextUtils.isEmpty(genreString)){
                    boldWithText("Genres : ", genreString, movieGenre);
                }
            }
            if(movieDetail.getYoutubeTrailerId() != null && !TextUtils.isEmpty(movieDetail.getYoutubeTrailerId())){
                youtubeTrailer.getSettings().setJavaScriptEnabled(true);
                String html = "<iframe width=\"100%\" height=\"100%\" src=\""+movieDetail.getYoutubeTrailerId()+"\"frameborder=\"0\" allowfullscreen></iframe>";
                youtubeTrailer.loadDataWithBaseURL("", html, "text/html", "UTF-8", null);
            } else {
                Glide.with(MovieDetailsActivity.this).load(movieDetail.getImage_url()).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL)).into(moviePoster);
            }
            boldWithText("Title : ", movieDetail.getTitle(), movieTitle);
            boldWithText("Plot/Synopsis : ", movieDetail.getSynopsis(), moviePlot);
            boldWithText("Number of Episodes : ", movieDetail.getEpisodes(), movieEpisodes);
            boldWithText("Rating : ", movieDetail.getRating(), movieRating);

            movieTitle.setText(movieDetail.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void boldWithText(String textInBold, String textToAppend, TextView textView){
        try {
            StringBuilder preText = new StringBuilder(textInBold);
            int endIndex = preText.length();
            preText.append(textToAppend);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(preText);
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            spannableStringBuilder.setSpan(boldSpan, 0,endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setText(spannableStringBuilder);
        } catch (Exception e) {
            e.printStackTrace();
            textView.setText("");
        }
    }

    private String appendStrings(List<SubMovie> subList){
        String appendedString = "";
        try {
            for(SubMovie sub : subList){
                appendedString+=", "+sub.getName();
            }
        } catch (Exception e){
            e.printStackTrace();
            return appendedString;
        }
        return appendedString;
    }
}