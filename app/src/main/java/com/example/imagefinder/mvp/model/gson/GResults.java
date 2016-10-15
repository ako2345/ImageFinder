package com.example.imagefinder.mvp.model.gson;

import java.util.List;

public class GResults {
    public List<Item> items;

    public class Item {
        public String title;
        public String link;
        public Image image;

        public class Image {
            public String thumbnailLink;
            public String contextLink;
            public int width;
            public int height;
        }
    }
}