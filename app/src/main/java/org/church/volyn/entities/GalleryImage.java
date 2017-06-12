package org.church.volyn.entities;

import java.util.List;

public class GalleryImage {
    private long id;
    private String mNewsId;
    private String mImageUrl;

    public GalleryImage(String newsId, String imageUrl) {
        mNewsId = newsId;
        mImageUrl = imageUrl;
    }

    public String getNewsId() {
        return mNewsId;
    }

    public void setNewsId(String newsId) {
        mNewsId = newsId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GalleryImage category = (GalleryImage) o;

        return id == category.id;

    }

}
