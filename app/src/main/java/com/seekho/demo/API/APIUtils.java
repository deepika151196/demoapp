package com.seekho.demo.API;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class APIUtils {
    static RequestQueue queue;

    public static void initializeRequestQueue(Context context){
        queue = Volley.newRequestQueue(context);
    }



    public static void requestMovieList(Response.Listener<JSONObject> response, Response.ErrorListener errorListener, int currentPage){
        try{
            String url = "https://api.jikan.moe/v4/top/anime";
            if(currentPage > 0){
                url = url + "?page="+(currentPage+1);
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, response, errorListener);
            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void requestAnimeDetails(Response.Listener<JSONObject> response, Response.ErrorListener errorListener, String animeId){
        try {
            String url = "https://api.jikan.moe/v4/anime/"+animeId;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, response, errorListener);
            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
