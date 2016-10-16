package com.example.imagefinder.mvp.model.gson;

import java.util.List;

public class BingSearchResults {
    public List<Image> value;

    public class Image {
        public String thumbnailUrl;
        public String hostPageDisplayUrl;
        public int width;
        public int height;
        public Thumbnail thumbnail;

        public class Thumbnail {
            public int width;
            public int height;
        }
    }
}
