package org.church.volyn.entities;

import org.church.volyn.App;
import org.church.volyn.utils.ResourcesUtils;

import java.util.List;

public class Gallery {
    private long id;
    private String mNewsId;
//    private String mImageUrl;
    private List<String> mImageUrls;

    public Gallery() {
    }

    public Gallery(long id, String newsId) {
        this.id = id;
        mNewsId = newsId;
//        mImageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public Gallery setId(long id) {
        this.id = id;
        return this;
    }

    public String getNewsId() {
        return mNewsId;
    }

    public void setNewsId(String newsId) {
        mNewsId = newsId;
    }

//    public String getImageUrl() {
//        return mImageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        mImageUrl = imageUrl;
//    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        mImageUrls = imageUrls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gallery category = (Gallery) o;

        return id == category.id;

    }

}
