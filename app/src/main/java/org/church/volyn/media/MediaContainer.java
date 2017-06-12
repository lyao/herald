package org.church.volyn.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 21.09.2015.
 */
public class MediaContainer {
    String mTitle;
    List<MediaElement> mMediaElements;
    int mId;
    String mDrawableId;
    int mType;

    public static final int TYPE_VIDEO = 0;
    public static final int TYPE_AUDIO = 1;

    public MediaContainer(int id, String title) {
        mId = id;
        mTitle = title;
        mMediaElements = new ArrayList<>();
    }

    public boolean isEmpty() {

        if (mDrawableId == null && mMediaElements.size() == 0) {
            return true;
        }
        return false;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getId() {
        return mId;
    }

    public void setOrder(int order) {
        mId = order;
    }

    public void addMediaElement(MediaElement mediaElement) {
        mMediaElements.add(mediaElement);
    }

    public List<MediaElement> getMediaElements() {
        return mMediaElements;
    }

    public String getDrawableId() {
        return mDrawableId;
    }

    public void setDrawableId(String drawableId) {
        mDrawableId = drawableId;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        this.mType = type;
    }
}
