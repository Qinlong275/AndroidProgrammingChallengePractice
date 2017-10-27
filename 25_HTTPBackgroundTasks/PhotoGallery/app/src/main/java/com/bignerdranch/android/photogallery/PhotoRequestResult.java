package com.bignerdranch.android.photogallery;

import java.util.List;

/**
 * Created by 秦龙 on 2017/9/27.
 */

public class PhotoRequestResult {
    PhotoResults photos;
    String stat;
    List<GalleryItem> getResults() {
        return photos.getPhotolist();
    }
    int getPageCount() {
        return photos.getMaxPages();
    }
    int getItemCount() {
        return photos.getTotal();
    }
    int getItemsPerPage() {
        return photos.getItemsPerPage();
    }
}
