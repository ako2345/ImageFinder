package com.example.imagefinder.mvp.model;

public class ImageInfo {
    public String thumbnailLink;
    public String contextLink;
    public int width;
    public int height;
    public int thumbnailWidth;
    public int thumbnailHeight;

    public ImageInfo(String thumbnailLink, String contextLink, int width, int height, int thumbnailWidth, int thumbnailHeight) {
        this.thumbnailLink = thumbnailLink;
        this.contextLink = contextLink;
        this.width = width;
        this.height = height;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }
}
