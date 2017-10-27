package com.bignerdranch.android.photogallery;

public class GalleryItem {
    private String title;
    private String id;
    private String url_s;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl_s() {
        return url_s;
    }

    public void setUrl_s(String url_s) {
        this.url_s = url_s;
    }

    @Override
    public String toString() {
        return title;
    }
}
