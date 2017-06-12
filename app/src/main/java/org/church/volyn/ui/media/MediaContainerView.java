package org.church.volyn.ui.media;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import org.church.volyn.R;
import org.church.volyn.media.MediaElement;

/**
 * Created by user on 24.09.2015.
 */
public class MediaContainerView extends RelativeLayout {

    List<MediaElement> mMediaElements;
    RecyclerView mRecyclerView;
    String mDrawableId;
    SwipeRefreshLayout mRefreshMedia;

    public MediaContainerView(Context context) {
        super(context);
        initializeViews(context);
    }

    public MediaContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public MediaContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.media_container, this);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.media_recycle_view);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mRefreshMedia.setEnabled(true);
                } else {
                    mRefreshMedia.setEnabled(false);
                }

            }
        });
        mMediaElements = new ArrayList<>();

    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        mRecyclerView.setLayoutManager(layout);
    }

    public void setRefreshMedia(SwipeRefreshLayout refreshMedia) {
        this.mRefreshMedia = refreshMedia;
    }

    public void setDrawableId(String drawableId) {
        mDrawableId = drawableId;
    }

    public String getDrawableId() {
        return mDrawableId;
    }
}
