package com.seekho.demo.Pojo;

import java.io.Serializable;

public class SubMovie implements Serializable {
    private String mal_id;
    private String type;
    private String name;
    private String url;

    public SubMovie(String mal_id, String type, String name, String url) {
        this.mal_id = mal_id;
        this.type = type;
        this.name = name;
        this.url = url;
    }

    public String getMal_id() {
        return mal_id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
