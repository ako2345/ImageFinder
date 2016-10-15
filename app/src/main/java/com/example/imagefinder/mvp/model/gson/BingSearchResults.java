package com.example.imagefinder.mvp.model.gson;

import java.util.List;

public class BingSearchResults {
    public List<Image> value;

    public class Image {
        public String thumbnailUrl;
        public String hostPageUrl;
        public int width;
        public int height;
    }
}
