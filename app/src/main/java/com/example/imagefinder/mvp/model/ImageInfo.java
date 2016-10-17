package com.example.imagefinder.mvp.model;

import java.util.List;

/**
 * Class representing list of image search results.
 */
public class ImageInfo {
    public String thumbnailLink;
    public String contextLink;
    public int width;
    public int height;
    public int thumbnailWidth;
    public int thumbnailHeight;
    public List<String> associatedImagesList;

    public ImageInfo(String thumbnailLink, String contextLink, int width, int height, int thumbnailWidth, int thumbnailHeight) {
        this.thumbnailLink = thumbnailLink;
        this.contextLink = contextLink;
        this.width = width;
        this.height = height;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    public void setAssociatedImagesList(List<String> associatedImagesList) {
        this.associatedImagesList = associatedImagesList;
    }
}
