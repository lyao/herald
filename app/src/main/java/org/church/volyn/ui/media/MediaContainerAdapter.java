package org.church.volyn.ui.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.church.volyn.media.MediaContainer;
import org.church.volyn.utils.ResourcesUtils;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.church.volyn.Constants;
import org.church.volyn.R;
import org.church.volyn.media.MediaElement;
import org.church.volyn.utils.BitmapUtils;

/**
 * Created by user on 26.09.2015.
 */
public class MediaContainerAdapter extends RecyclerView.Adapter<MediaContainerAdapter.BaseMediaViewHolder> {

    Context mContext;
    List<MediaElement> mDataSet;

    private OnMediaItemClickListener mOnMediaItemClickListener;
    private String mDrawableId;

    private String mContainerTitle;
    private int mContainerType;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final ThumbnailListener thumbnailListener;
    private static final int MEDIA_TITLE_VIEW = 0;
    private static final int MEDIA_THUMBNAIL_VIEW = 1;


    public interface OnMediaItemClickListener {
        public void onMediaItemClick(String mediaSource, String mediaTitle);
    }

    public void setOnMediaItemClickListener(OnMediaItemClickListener onMediaItemClickListener) {
        mOnMediaItemClickListener = onMediaItemClickListener;
    }

    public MediaContainerAdapter(Context context, List<MediaElement> dataSet) {
        mContext = context;
        mDataSet = new ArrayList<>(dataSet);
        mDataSet.add(0, new MediaElement());
        thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
        thumbnailListener = new ThumbnailListener();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 ? 0 : 1);
    }

    @Override
    public BaseMediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case MEDIA_TITLE_VIEW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.media_element_title, parent, false);
                return new MediaTitleViewHolder(v);
            case MEDIA_THUMBNAIL_VIEW:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.media_element, parent, false);
                return new MediaViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseMediaViewHolder holder, int position) {
        MediaElement mediaElement = mDataSet.get(position);
   //     holder.bindMediaElement(mediaElement, position);
        switch (getItemViewType(position)) {
            case MEDIA_TITLE_VIEW:
                MediaTitleViewHolder mediaTitleViewHolder = (MediaTitleViewHolder) holder;
                mediaTitleViewHolder.bindMediaElement(mediaElement, position);
                break;
            case MEDIA_THUMBNAIL_VIEW:
                MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
                mediaViewHolder.bindMediaElement(mediaElement, position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public String getDrawableId() {
        return mDrawableId;
    }

    public void setDrawableId(String drawableId) {
        mDrawableId = drawableId;
    }

    public String getTitle() {
        return mContainerTitle;
    }

    public void setTitle(String title) {
        mContainerTitle = title;
    }

    public int getContainerType() {
        return mContainerType;
    }

    public void setContainerType(int containerType) {
        this.mContainerType = containerType;
    }

    public abstract class BaseMediaViewHolder extends RecyclerView.ViewHolder {

        public BaseMediaViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindMediaElement(MediaElement mediaElement, int position);

    }

    public class MediaViewHolder extends BaseMediaViewHolder implements View.OnClickListener {

        TextView mTitleTextView;
        MediaElement mMediaElement;
        ViewGroup mBackground;
        YouTubeThumbnailView mThumbnailView;

        public MediaViewHolder(View v) {
            super(v);
            mThumbnailView = (YouTubeThumbnailView) v.findViewById(R.id.youtube_thumbnail);
            mTitleTextView = (TextView) v.findViewById(R.id.media_title);
//            mThumbnailView.setTag(mMediaElement.getYid());
            mThumbnailView.initialize(Constants.YOUTUBE_DEVELOPER_KEY, thumbnailListener);
            mBackground = (ViewGroup) v;
        }

        @Override
        public void bindMediaElement(MediaElement mediaElement, int position) {
            mMediaElement = mediaElement;
            mTitleTextView.setText(mContainerTitle + "\n" + getLocalDate(mediaElement.getDate()));
            YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(mThumbnailView);
            if (loader == null) {
                mThumbnailView.setTag(mediaElement.getYid());
            } else {
                loader.setVideo(mediaElement.getYid());
            }
            mBackground.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnMediaItemClickListener != null) {
                mOnMediaItemClickListener.onMediaItemClick(mMediaElement.getYid(), mContainerTitle + " " + getLocalDate(mMediaElement.getDate()));
            }
        }
    }

    public class MediaTitleViewHolder extends BaseMediaViewHolder {

        TextView mMediaTitleTextView;
        //        MediaElement mMediaElement;
        RelativeLayout mBackground;
        ImageView mMediaImageView;
        ImageView mMediaTypeImageView;
//        YouTubeThumbnailView mThumbnailView;

        public MediaTitleViewHolder(View v) {
            super(v);
//            mThumbnailView = (YouTubeThumbnailView) v.findViewById(R.id.youtube_thumbnail);
//            mThumbnailView.setTag(mMediaElement.getYid());
//            mThumbnailView.initialize(Constants.YOUTUBE_DEVELOPER_KEY, thumbnailListener);
            mBackground = (RelativeLayout) v;
            mMediaTitleTextView = (TextView) v.findViewById(R.id.media_title);
            mMediaImageView = (ImageView) v.findViewById(R.id.media_image);
            mMediaTypeImageView = (ImageView) v.findViewById(R.id.media_type);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void bindMediaElement(MediaElement mediaElement, int position) {

//            mMediaElement = mediaElement;
            Drawable backgoundDrawable;

            backgoundDrawable = BitmapUtils.getDrawableByName(mBackground.getContext(), mDrawableId);
            mBackground.setOnClickListener(null);
            String title = ResourcesUtils.getStringByName(mContext, mDrawableId);
            mMediaTitleTextView.setText(title);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mMediaImageView.setBackgroundDrawable(backgoundDrawable);
            } else {
                mMediaImageView.setBackground(backgoundDrawable);
            }
            if (mContainerType == MediaContainer.TYPE_VIDEO) {
                mMediaTypeImageView.setImageResource(R.drawable.video_icon);
            } else {
                mMediaTypeImageView.setImageResource(R.drawable.audio_icon);
            }
        }
    }

    private String getLocalDate (Date date) {
        Date today;
        String dateOut;
        DateFormat dateFormatter;
        Locale currentLocale = Locale.getDefault();
        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, currentLocale);
        dateOut = dateFormatter.format(date);
        return dateOut;
    }

    private final class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(
                YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view, loader);
//            view.setImageResource(R.drawable.loading_thumbnail);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(
                YouTubeThumbnailView view, YouTubeInitializationResult loader) {
//            view.setImageResource(R.drawable.no_thumbnail);
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view, YouTubeThumbnailLoader.ErrorReason errorReason) {
//            view.setImageResource(R.drawable.no_thumbnail);
        }
    }
}
