package com.example.imagefinder.mvp.model;

public class ImageInfo {
    public String thumbnailLink;
    public String contextLink;
    public int width;
    public int height;

    public ImageInfo(String thumbnailLink, String contextLink, int width, int height) {
        this.thumbnailLink = thumbnailLink;
        this.contextLink = contextLink;
        this.width = width;
        this.height = height;
    }
}
