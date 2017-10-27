package com.bignerdranch.android.photogallery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 秦龙 on 2017/9/27.
 */

public class PhotoResults {
    int page;
    int pages;
    int perpage;
    int total;
    @SerializedName("photo")
    List<GalleryItem> photolist;

    List<GalleryItem> getPhotolist() {
        return photolist;
    }
    int getItemsPerPage() {
        return perpage;
    }
    int getMaxPages() {
        return pages;
    }
    int getTotal() {
        return total;
    }
}
