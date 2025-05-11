package com.seekho.demo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.seekho.demo.API.APIUtils;
import com.seekho.demo.Pojo.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MovieListActivity extends AppCompatActivity {
    private RecyclerView movieListRecyclerView;
    private TextView noDataView;
    private ProgressBar loader;
    private List<Movie> movieList = new ArrayList<>();
    private MovieListAdapter movieListAdapter;
    private int currentPage = 0;
    private boolean hasNextPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        getSupportActionBar().hide();
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {

        });
        APIUtils.initializeRequestQueue(MovieListActivity.this);
        requestMovieList();
        init();
    }

    private void init(){
        try {
            movieListRecyclerView = findViewById(R.id.movie_list_recycler_view);
            movieListRecyclerView.setLayoutManager(new LinearLayoutManager(MovieListActivity.this, RecyclerView.VERTICAL, false));
            movieListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(!movieListRecyclerView.canScrollVertically(1)){
                        onEndReached(true);
                    }
                }
            });
            movieListAdapter = new MovieListAdapter();
            movieListRecyclerView.setAdapter(movieListAdapter);
            movieListRecyclerView.setVisibility(GONE);
            noDataView = findViewById(R.id.no_data_view);
            noDataView.setVisibility(GONE);
            loader = findViewById(R.id.loader_view);
            loader.setVisibility(VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestMovieList(){
        try {
            Response.Listener<JSONObject> response = response1 -> {
                try {
                    if (response1 == null && (movieList == null || movieList.size() == 0)) {
                        updateUI();
                        return;
                    }
                    JSONObject paginationObject = response1.getJSONObject("pagination");
                    if (paginationObject.has("current_page") && paginationObject.getInt("current_page") > 0) {
                        currentPage = paginationObject.getInt("current_page");
                    }
                    if (paginationObject.has("has_next_page") && paginationObject.getBoolean("has_next_page")) {
                        hasNextPage = paginationObject.getBoolean("has_next_page");
                    }
                    if(movieList == null){
                        movieList = new ArrayList<>();
                    }
                    if(response1.has("data") && response1.getJSONArray("data").length() > 0) {
                        JSONArray movieArray = response1.getJSONArray("data");
                        List<Movie> tempMoviesList = new ArrayList<>();
                        tempMoviesList.addAll(IntStream.range(0, movieArray.length())
                                .mapToObj(index -> {
                                    try {
                                        return movieArray.getJSONObject(index);
                                    } catch (JSONException e) {
                                        return new JSONObject();
                                    }
                                })
                                .map(jsonObject -> {
                                    try {
                                        return new Movie(jsonObject.getString("mal_id")
                                        , jsonObject.getString("title")
                                        , jsonObject.getString("episodes")
                                        , jsonObject.getString("rating")
                                        , jsonObject.getJSONObject("images").getJSONObject("jpg").getString("image_url"));
                                    } catch (JSONException e) {
                                       return null;
                                    }
                                }).collect(Collectors.toList()));
                        if(tempMoviesList != null && tempMoviesList.size() > 0){
                            movieList.addAll(tempMoviesList);
                        }
                        updateUI();
                    } else {
                        updateUI();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            };
            Response.ErrorListener errorListener = error -> {
                movieList = new ArrayList<>();
                updateUI();
            };
            APIUtils.requestMovieList(response, errorListener, currentPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(){
        try {
            loader.setVisibility(GONE);
            movieListAdapter.notifyDataSetChanged();
            if(movieList == null || movieList.size() == 0){
                movieListRecyclerView.setVisibility(GONE);
                noDataView.setVisibility(VISIBLE);
            } else {
                movieListRecyclerView.setVisibility(VISIBLE);
                noDataView.setVisibility(GONE);
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onEndReached(boolean isReachedBottom){
        try{
            if(isReachedBottom && hasNextPage && loader.getVisibility() == GONE){
                loader.setVisibility(VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                requestMovieList();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    class MovieListAdapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                holder.movieTitle.setText(movieList.get(position).getTitle());
                holder.movieEpisodes.setText("No. of Episodes : "+movieList.get(position).getEpisodes());
                holder.movieRating.setText("Rating : "+movieList.get(position).getTitle());
                Glide.with(MovieListActivity.this)
                        .load(movieList.get(position).getImage_url())
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .into(holder.moviePoster);
                holder.itemView.setOnClickListener(v -> {
                    Intent detailIntent = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
                    detailIntent.putExtra("anime_id", movieList.get(position).getMal_id());
                    startActivity(detailIntent);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return movieList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView moviePoster;
        private TextView movieTitle;
        private TextView movieEpisodes;
        private TextView movieRating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.moviePoster = itemView.findViewById(R.id.movie_poster);
            this.movieTitle = itemView.findViewById(R.id.movie_title);
            this.movieEpisodes = itemView.findViewById(R.id.movie_number_of_episodes);
            this.movieRating = itemView.findViewById(R.id.movie_rating);
        }
    }
}