package org.church.volyn.media;

import java.util.Date;

/**
 * Created by user on 21.09.2015.
 */
public class MediaElement {
    Integer mCatId;
    String mYid;
    Date mDate;
    String mTitle;

    public Integer getCatId() {
        return mCatId;
    }

    public void setCatId(Integer catId) {
        this.mCatId = catId;
    }

    public String getYid() {
        return mYid;
    }

    public void setYid(String yid) {
        mYid = yid;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
