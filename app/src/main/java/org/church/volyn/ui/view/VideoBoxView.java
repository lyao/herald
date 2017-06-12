package org.church.volyn.ui.view;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.church.volyn.R;
import org.church.volyn.ui.media.MediaPlayerFragment;
import static org.church.volyn.Constants.*;

public class VideoBoxView extends RelativeLayout {

    private Context mContext;
    private String mYoutubeUrl;
    private ItemHolder mItemHolder;
    private onCloseButtonListener mOnCloseButtonListener;
    private View mAnchor;

    public interface onCloseButtonListener {
        void onCloseButtonClick();
    }

    public VideoBoxView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public VideoBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public VideoBoxView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(
//                attrs, R.styleable.VideoBoxView, defStyle, 0);
//
//        a.recycle();
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.video_box_view, this);
        mItemHolder = new ItemHolder();
        mItemHolder.closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnCloseButtonListener != null) {
                    mOnCloseButtonListener.onCloseButtonClick();
                }
            }
        });
    }

    public String getYoutubeUrl() {
        return mYoutubeUrl;
    }

    public void setYoutubeUrl(String mYoutubeUrl) {
        this.mYoutubeUrl = mYoutubeUrl;
    }

    public void setMediaTitle(String title) {
        mItemHolder.title.setText(title);
    }

    public void play(String url) {
        mItemHolder.mediaPlayerFragment.play(url);
    }

    public void pause() {
        mItemHolder.mediaPlayerFragment.pause();
    }

    public void setMediaTitleVisibility(int visibility) {
        mItemHolder.title.setVisibility(visibility);
    }

    public void setCloseButtonVisibility(int visibility) {
        mItemHolder.closeButton.setVisibility(visibility);
    }

    public void setOnCloseButtonListener(onCloseButtonListener onCloseButtonListener) {
        this.mOnCloseButtonListener = onCloseButtonListener;
    }

    public void setAnchor(View mAnchor) {
        this.mAnchor = mAnchor;
    }

    public void show() {
        if (getVisibility() != View.VISIBLE) {
            setTranslationY(-getHeight());
            setVisibility(View.VISIBLE);
        }

        if (getTranslationY() < 0) {
            animate().translationY(0).setDuration(ANIMATION_VIDEOBOX_MOVING);
            mAnchor.animate().translationY(getHeight()).setDuration(ANIMATION_VIDEOBOX_MOVING);
        }
    }

    public void hide() {
        animate().translationYBy(-getHeight()).setDuration(ANIMATION_VIDEOBOX_MOVING);
        mAnchor.animate().translationYBy(-getHeight()).setDuration(ANIMATION_VIDEOBOX_MOVING);
    }

    private class ItemHolder {
        public final ImageView closeButton;
        public final TextView title;
        MediaPlayerFragment mediaPlayerFragment;

        public ItemHolder() {
            closeButton = (ImageView) findViewById(R.id.video_close_button);
            title = (TextView) findViewById(R.id.media_title);
            mediaPlayerFragment = (MediaPlayerFragment)((AppCompatActivity)mContext).getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        }
    }

}
