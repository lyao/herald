package org.church.volyn.entities;

import org.church.volyn.Constants;

/**
 * Created by user on 18.11.2014.
 */
public class NewsItem {
    private String mTitle;
    private String mLink;
    private String mNewsContent;
    private String mRawNewsContent;
    private long mPubDate;
    private String mImageUrl;
    private String mVideoUrl;
    private Category mCategory;
    private long mOrder;
    private String mGuid;
    private long mStatus = Constants.ACTIVE;

    public NewsItem() {}

    public NewsItem(String title, String description, String link) {
        mTitle = title;
        mLink = link;
    }

    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String mGuid) {
        this.mGuid = mGuid;
    }

    public String getNewsLink() {
        return mLink;
    }

    public void setNewsLink(String link) {
        this.mLink = link;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public long getPubDate() {
        return mPubDate;
    }

    public void setPubDate(long pubDate) {
        this.mPubDate = pubDate;
    }

    public String getNewsContent() {
        return mNewsContent;
    }

    public void setNewsContent(String newsContent) {
        this.mNewsContent = newsContent;
    }

    public String getRawNewsContent() {
        return mRawNewsContent;
    }

    public void setRawNewsContent(String rawNewsContent) {
        mRawNewsContent = rawNewsContent;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    public long getOrder() {
        return mOrder;
    }

    public void setOrder(long mOrder) {
        this.mOrder = mOrder;
    }

    public long getStatus() {
        return mStatus;
    }

    public void setStatus(long status) {
        this.mStatus = status;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public void setVideoUrl(String mVideoUrl) {
        this.mVideoUrl = mVideoUrl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.getNewsLink() == null) ? 0 : this.getNewsLink().toUpperCase().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        NewsItem other = (NewsItem) obj;
        if (this.getNewsLink() == null) {
            if (other.getNewsLink() != null) {
                return false;

            }
        } else if (!this.getNewsLink().equalsIgnoreCase(other.getNewsLink())) {
            return false;
        }
        return true;
    }
}
