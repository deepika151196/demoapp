package com.seekho.demo.Pojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Movie implements Serializable {
    private String mal_id;
    private String title;
    private String episodes;
    private String rating;
    private String image_url;
    private String synopsis;
    private String youtubeTrailerId;
    private List<SubMovie> genres;
    private List<SubMovie> producers;
    private List<SubMovie> studios;

    public String getMal_id() {
        return mal_id;
    }

    public void setMal_id(String mal_id) {
        this.mal_id = mal_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getYoutubeTrailerId() {
        return youtubeTrailerId;
    }

    public List<SubMovie> getGenres() {
        return genres;
    }

    public List<SubMovie> getProducers() {
        return producers;
    }

    public List<SubMovie> getStudios() {
        return studios;
    }

    public Movie(String mal_id, String title, String episodes, String rating, String image_url) {
        this.mal_id = mal_id;
        this.title = title;
        this.episodes = episodes;
        this.rating = rating;
        this.image_url = image_url;
    }

    public Movie(String mal_id, String title, String episodes, String rating, String image_url, String synopsis, String youtubeTrailerId, JSONArray genres, JSONArray producers, JSONArray studios) {
        this.mal_id = mal_id;
        this.title = title;
        this.episodes = episodes;
        this.rating = rating;
        this.image_url = image_url;
        this.synopsis = synopsis;
        this.youtubeTrailerId = youtubeTrailerId;
        this.genres = convertJsonToList(genres);
        this.producers = convertJsonToList(producers);
        this.studios = convertJsonToList(studios);
    }

    private List<SubMovie> convertJsonToList(JSONArray jsonArray){
        try{
            List<SubMovie> convertedList = IntStream.range(0, jsonArray.length())
                    .mapToObj(index -> {
                        try {
                            return jsonArray.getJSONObject(index);
                        } catch (JSONException e) {
                            return new JSONObject();
                        }
                    })
                    .map(jsonObject -> {
                        SubMovie subMovie = null;
                        try {
                            subMovie = new SubMovie(
                                    jsonObject.getString("mal_id")
                                    ,jsonObject.getString("type")
                                    ,jsonObject.getString("name")
                                    ,jsonObject.getString("url"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return subMovie;
                    })
                    .collect(Collectors.toList());
            return convertedList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
